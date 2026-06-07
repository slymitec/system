package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.ACacheableObject;
import indi.sly.subsystem.periphery.core.prototypes.AFactory;
import indi.sly.subsystem.periphery.proxies.instances.core.DateTimeProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.ProxyProcessorMediator;
import indi.sly.subsystem.periphery.proxies.prototypes.processors.IProxyResolver;
import indi.sly.subsystem.periphery.proxies.prototypes.processors.ProxyCallResolver;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyFactory extends AFactory {
    public ProxyFactory() {
        this.cacheableObjectNames = new ConcurrentHashMap<>();
        this.proxyResolvers = new CopyOnWriteArrayList<>();

        this.addCacheableObjectName(DateTimeProxyObject.class);
    }

    private final List<IProxyResolver> proxyResolvers;

    @Override
    public void init() {
        this.proxyResolvers.add(this.coreManager.create(ProxyCallResolver.class));

        Collections.sort(this.proxyResolvers);
    }

    private final Map<String, Class<? extends ACacheableObject<?>>> cacheableObjectNames;

    private void addCacheableObjectName(Class<? extends ACacheableObject<?>> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        this.cacheableObjectNames.put(ClassUtil.getSimpleName(clazz), clazz);
    }

    private Class<? extends ACacheableObject<?>> getCacheableObjectClazz(String clazzName) {
        Class<? extends ACacheableObject<?>> cacheableObjectClazz = this.cacheableObjectNames.getOrDefault(clazzName, null);
        if (ObjectUtil.isAnyNull(cacheableObjectClazz)) {
            throw new StatusNotExistedException();
        } else {
            return cacheableObjectClazz;
        }
    }

    public AProxy buildProxy(String clazzName) {
        ProxyProcessorMediator processorMediator = this.coreManager.create(ProxyProcessorMediator.class);
        for (IProxyResolver processCreatorResolver : this.proxyResolvers) {
            processCreatorResolver.resolve(null, processorMediator);
        }

        return null;

    }
}
