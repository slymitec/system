package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AComponent;
import indi.sly.subsystem.periphery.proxies.instances.IProxy;
import indi.sly.system.common.lang.StatusNotSupportedException;
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

        if (methodName.equals("getHandle") && (ObjectUtil.isNull(args) || args.length == 0)) {
            UUID handle = ObjectUtil.transferFromString(UUID.class, this.remote.getValue());

            if (ValueUtil.isNullOrEmpty(handle)) {
                throw new StatusUnexpectedException();
            }

            return handle;
        } else if (methodName.equals("die") && (ObjectUtil.isNull(args) || args.length == 0)) {
            this.remote.invoke("unCache", Void.class);

            return null;
        } else if (methodName.equals("toString") && (ObjectUtil.isNull(args) || args.length == 0)) {
            return this.remote.getValue();
        } else if (methodName.equals("hashCode") && (ObjectUtil.isNull(args) || args.length == 0)) {
            return System.identityHashCode(proxy);
        } else if (methodName.equals("equals") && (ObjectUtil.notNull(args) && args.length == 1)) {
            return proxy.equals(args[0]);
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

        switch (type) {
            case Class<?> clazz -> {
                if (ClassUtil.isThisOrSuperContain(clazz, IProxy.class) && arg instanceof IProxy proxy) {
                    return proxy.getHandle();
                } else {
                    return arg;
                }
            }
            case ParameterizedType parameterizedType -> {
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
            }
            case WildcardType wildcardType -> {
                Type[] upperBounds = wildcardType.getUpperBounds();

                return upperBounds.length > 0 ? this.convertArgument(arg, upperBounds[0]) : arg;
            }
            case TypeVariable<?> typeVariable -> {
                Type[] bounds = typeVariable.getBounds();

                return bounds.length > 0 ? this.convertArgument(arg, bounds[0]) : arg;
            }
            case GenericArrayType genericArrayType when arg.getClass().isArray() -> {
                int len = Array.getLength(arg);

                Type componentType = genericArrayType.getGenericComponentType();

                Object[] converted = new Object[len];
                for (int i = 0; i < len; i++) {
                    converted[i] = this.convertArgument(Array.get(arg, i), componentType);
                }

                Class<?> outputComponentClass = this.inferComponentClass(converted, componentType);

                Object out = Array.newInstance(outputComponentClass, len);
                for (int i = 0; i < len; i++) {
                    Array.set(out, i, converted[i]);
                }

                return out;
            }
            default -> {
                return arg;
            }
        }
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
                Type[] upperBounds = wildcardType.getUpperBounds();
                return upperBounds.length > 0 ? erase(upperBounds[0]) : null;
            }
            case TypeVariable<?> typeVariable -> {
                Type[] bounds = typeVariable.getBounds();
                return bounds.length > 0 ? erase(bounds[0]) : null;
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
        switch (type) {
            case Class<?> clazz -> {
                if (clazz.equals(Void.TYPE) || clazz.equals(Void.class)) {
                    return null;
                } else if (ClassUtil.isThisOrSuperContain(clazz, IProxy.class)) {
                    return this.factory.buildProxy((Class<? extends IProxy>) clazz, remote);
                } else {
                    return this.factory.getValue(clazz, remote);
                }
            }
            case ParameterizedType parameterizedType -> {
                Type rawType = parameterizedType.getRawType();
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

                Class<?> rawClass = this.erase(rawType);
                if (rawClass == null) {
                    return this.factory.getValue(Object.class, remote);
                } else if (actualTypeArguments.length == 0) {
                    return this.factory.getValue(rawClass, remote);
                } else {
                    Class<?> elemClass = this.erase(actualTypeArguments[0]);
                    boolean isProxyElem = elemClass != null && ClassUtil.isThisOrSuperContain(elemClass, IProxy.class);

                    if (isProxyElem) {
                        if (ClassUtil.isThisOrSuperContain(rawClass, Set.class)) {
                            return this.factory.buildProxySet((Class<? extends IProxy>) elemClass, remote);
                        } else if (ClassUtil.isThisOrSuperContain(rawClass, List.class) || ClassUtil.isThisOrSuperContain(rawClass, Collection.class)) {
                            return this.factory.buildProxyList((Class<? extends IProxy>) elemClass, remote);
                        } else if (ClassUtil.isThisOrSuperContain(rawClass, Queue.class)) {
                            return this.factory.buildProxyQueue((Class<? extends IProxy>) elemClass, remote);
                        } else {
                            throw new StatusNotSupportedException();
                        }
                    }

                    return this.factory.getValue(rawClass, remote);
                }
            }
            case WildcardType wildcardType -> {
                Type[] upperBounds = wildcardType.getUpperBounds();

                return upperBounds.length > 0
                        ? this.buildReturnValue(upperBounds[0], remote)
                        : this.factory.getValue(Object.class, remote);
            }
            case TypeVariable<?> typeVariable -> {
                Type[] bounds = typeVariable.getBounds();

                return bounds.length > 0 ? this.buildReturnValue(bounds[0], remote) : this.factory.getValue(Object.class, remote);
            }
            case GenericArrayType genericArrayType -> {
                Class<?> comp = this.erase(genericArrayType.getGenericComponentType());

                Class<?> arrayClass = comp != null ? Array.newInstance(comp, 0).getClass() : Object[].class;

                return this.factory.getValue(arrayClass, remote);
            }
            default -> {
                return this.factory.getValue(Object.class, remote);
            }
        }
    }

    private @Nullable Class<?> inferComponentClass(Object[] converted, Type declaredComponentType) {
        Class<?> common = null;

        for (Object e : converted) {
            if (ObjectUtil.isNull(e)) {
                continue;
            }
            Class<?> c = e.getClass();
            if (ObjectUtil.isNull(common)) {
                common = c;
            } else if (common != c) {
                common = this.commonSuperClass(common, c);
                if (common == Object.class) {
                    return Object.class;
                }
            }
        }

        if (ObjectUtil.isNull(common)) {
            return common;
        } else {
            Class<?> erased = this.erase(declaredComponentType);

            return erased != null ? erased : Object.class;
        }
    }

    private Class<?> commonSuperClass(Class<?> a, Class<?> b) {
        if (a.isAssignableFrom(b)) {
            return a;
        } else if (b.isAssignableFrom(a)) {
            return b;
        } else {
            for (Class<?> c = a.getSuperclass(); c != null; c = c.getSuperclass()) {
                if (c.isAssignableFrom(b)) {
                    return c;
                }
            }
            return Object.class;
        }
    }
}