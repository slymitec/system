package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AComponent;
import indi.sly.subsystem.periphery.proxies.instances.IProxy;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.*;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyComponent extends AComponent implements InvocationHandler {
    @SuppressWarnings("NullAway.Init")
    protected ProxyFactory factory;
    @SuppressWarnings("NullAway.Init")
    protected RemoteObject remote;

    @Override
    public @Nullable Object invoke(Object proxy, Method method, Object @Nullable [] args) throws Throwable {
        String methodName = method.getName();

        if (methodName.equals("getHandle")) {
            UUID handle = ObjectUtil.transferFromString(UUID.class, this.remote.getValue());

            if (ValueUtil.isNullOrEmpty(handle)) {
                throw new StatusUnexpectedException();
            }

            return handle;
        } else if (methodName.equals("die")) {
            this.remote.invoke("unCache", Void.class);

            return null;
        } else if (methodName.equals("toString")) {
            return this.remote.getValue();
        } else if (methodName.equals("hashCode")) {
            return System.identityHashCode(proxy);
        } else if (methodName.equals("equals")) {
            if (ObjectUtil.notNull(args) && args.length > 0) {
                return proxy.equals(args[0]);
            } else {
                return false;
            }
        } else {
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            Type returnType = method.getGenericReturnType();

            RemoteObject remote;

            if (ObjectUtil.notNull(args)) {
                for (int i = 0; i < args.length; i++) {
                    if (ObjectUtil.notNull(args[i])) {
                        args[i] = this.convertArgument(args[i], genericParameterTypes[i]);
                    }
                }

                remote = this.remote.invoke(methodName, args);
            } else {
                remote = this.remote.invoke(methodName);
            }

            return this.buildReturnValue(returnType, remote);
        }
    }

    private Object convertArgument(Object arg, Type type) {
        if (ObjectUtil.isAnyNull(arg, type)) {
            return arg;
        }

        if (type instanceof Class<?> clazz) {
            if (ClassUtil.isThisOrSuperContain(clazz, IProxy.class) && arg instanceof IProxy proxy) {
                return proxy.getHandle();
            } else {
                return arg;
            }
        } else if (type instanceof ParameterizedType parameterizedType) {
            Type rawType = parameterizedType.getRawType();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            if (!(rawType instanceof Class<?> rawClass) || actualTypeArguments.length == 0) {
                return arg;
            } else if (ClassUtil.isThisOrSuperContain(rawClass, Set.class)) {
                Set<Object> out = new LinkedHashSet<>();

                for (Object e : (Set<?>) arg) {
                    out.add(this.convertArgument(e, actualTypeArguments[0]));
                }

                return out;
            } else if (ClassUtil.isThisOrSuperContain(rawClass, List.class)) {
                List<Object> out = new ArrayList<>();

                for (Object e : (List<?>) arg) {
                    out.add(this.convertArgument(e, actualTypeArguments[0]));
                }

                return out;
            } else if (ClassUtil.isThisOrSuperContain(rawClass, Collection.class)) {
                Collection<Object> out = new ArrayList<>();

                for (Object e : (Collection<?>) arg) {
                    out.add(this.convertArgument(e, actualTypeArguments[0]));
                }

                return out;
            } else if (ClassUtil.isThisOrSuperContain(rawClass, Map.class)) {
                Map<Object, Object> out = new LinkedHashMap<>();

                for (Map.Entry<?, ?> e : ((Map<?, ?>) arg).entrySet()) {
                    out.put(this.convertArgument(e.getKey(), actualTypeArguments[0]), this.convertArgument(e.getValue(), actualTypeArguments[1]));
                }

                return out;
            } else {
                return arg;
            }
        } else if (type instanceof WildcardType wildcardType) {
            Type[] upperBounds = wildcardType.getUpperBounds();

            return upperBounds.length > 0 ? this.convertArgument(arg, upperBounds[0]) : arg;
        } else if (type instanceof TypeVariable<?> typeVariable) {
            Type[] bounds = typeVariable.getBounds();

            return bounds.length > 0 ? this.convertArgument(arg, bounds[0]) : arg;
        } else if (type instanceof GenericArrayType genericArrayType && arg.getClass().isArray()) {
            int len = Array.getLength(arg);

            Object out = Array.newInstance(erase(genericArrayType.getGenericComponentType()), len);
            for (int i = 0; i < len; i++) {
                Array.set(out, i, this.convertArgument(Array.get(arg, i), genericArrayType.getGenericComponentType()));
            }

            return out;
        }
        return arg;
    }

    private @Nullable Class<?> erase(Type type) {
        switch (type) {
            case Class<?> clazz -> {
                return clazz;
            }
            case ParameterizedType parameterizedType -> {
                return (Class<?>) parameterizedType.getRawType();
            }
            case WildcardType wildcardType -> {
                Type[] ub = wildcardType.getUpperBounds();
                return ub.length > 0 ? erase(ub[0]) : null;
            }
            case TypeVariable<?> typeVariable -> {
                Type[] b = typeVariable.getBounds();
                return b.length > 0 ? erase(b[0]) : null;
            }
            case GenericArrayType genericArrayType -> {
                Class<?> comp = erase(genericArrayType.getGenericComponentType());
                return comp != null ? Array.newInstance(comp, 0).getClass() : null;
            }
            default -> {
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private @Nullable Object buildReturnValue(Type type, RemoteObject remote) {
        if (type instanceof Class<?> clazz) {
            if (clazz.equals(Void.TYPE) || clazz.equals(Void.class)) {
                return null;
            } else if (ClassUtil.isThisOrSuperContain(clazz, IProxy.class)) {
                return this.factory.buildProxy((Class<? extends IProxy>) clazz, remote);
            } else {
                return this.factory.getValue(clazz, remote);
            }
        }

        if (type instanceof ParameterizedType parameterizedType) {
            Type rawType = parameterizedType.getRawType();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            Class<?> rawClass = this.erase(rawType);
            if (rawClass == null) {
                return this.factory.getValue(Object.class, remote);
            }

            if (actualTypeArguments.length == 0) {
                return this.factory.getValue(rawClass, remote);
            }

            Class<?> elemClass = this.erase(actualTypeArguments[0]);
            boolean isProxyElem = elemClass != null && ClassUtil.isThisOrSuperContain(elemClass, IProxy.class);

            if (isProxyElem) {
                if (ClassUtil.isThisOrSuperContain(rawClass, Set.class)) {
                    return this.factory.buildProxySet((Class<? extends IProxy>) elemClass, remote);
                }
                if (ClassUtil.isThisOrSuperContain(rawClass, List.class)) {
                    return this.factory.buildProxyList((Class<? extends IProxy>) elemClass, remote);
                }
            }

            return this.factory.getValue(rawClass, remote);
        }

        if (type instanceof WildcardType wildcardType) {
            Type[] upperBounds = wildcardType.getUpperBounds();

            return upperBounds.length > 0
                    ? this.buildReturnValue(upperBounds[0], remote)
                    : this.factory.getValue(Object.class, remote);
        }

        if (type instanceof TypeVariable<?> typeVariable) {
            Type[] bounds = typeVariable.getBounds();

            return bounds.length > 0
                    ? this.buildReturnValue(bounds[0], remote)
                    : this.factory.getValue(Object.class, remote);
        }

        if (type instanceof GenericArrayType genericArrayType) {
            Class<?> comp = this.erase(genericArrayType.getGenericComponentType());
            if (comp == null) {
                return this.factory.getValue(Object.class, remote);
            }

            Class<?> arrayClass = Array.newInstance(comp, 0).getClass();
            return this.factory.getValue(arrayClass, remote);
        }

        return this.factory.getValue(Object.class, remote);
    }
}