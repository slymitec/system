package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AFactory;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.prototypes.processors.*;
import indi.sly.subsystem.periphery.proxies.values.HandleTableDefinition;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyFactory extends AFactory {
    public ProxyFactory() {
        this.remoteResolvers = new CopyOnWriteArrayList<>();
        this.namedProxyClasses = new ConcurrentHashMap<>();

        //this.addCacheableObjectName(DateTimeProxyObject.class);
    }

    private final List<IRemoteResolver> remoteResolvers;
    private final Map<String, Class<? extends AProxyObject>> namedProxyClasses;

    @Override
    public void init() {
        this.remoteResolvers.add(this.coreManager.create(RemoteCallResolver.class));
        this.remoteResolvers.add(this.coreManager.create(RemoteCheckConditionResolver.class));
        this.remoteResolvers.add(this.coreManager.create(RemoteCheckExpiredResolver.class));
        this.remoteResolvers.add(this.coreManager.create(RemoteDateResolver.class));

        Collections.sort(this.remoteResolvers);
    }

//    private void addCacheableObjectName(Class<? extends AProxyObject> clazz) {
//        if (ObjectUtil.isAnyNull(clazz)) {
//            throw new ConditionParametersException();
//        }
//
//        this.namedProxyClasses.put(ClassUtil.getSimpleName(clazz), clazz);
//    }
//
//    public Class<? extends AProxyObject> getProxyClazz(String clazzName) {
//        Class<? extends ACacheableObject<?>> cacheableObjectClazz = this.namedProxyClasses.getOrDefault(clazzName, null);
//        if (ObjectUtil.isAnyNull(cacheableObjectClazz)) {
//            throw new StatusNotExistedException();
//        } else {
//            return cacheableObjectClazz;
//        }
//    }

    private RemoteObject create(RemoteProcessorMediator processorMediator, RemoteDefinition definition, ProcedureObject procedure) {
        RemoteObject remote = this.coreManager.create(RemoteObject.class);

        remote.setBase(procedure);
        remote.setDefinition(definition);
        remote.factory = this;
        remote.processorMediator = processorMediator;

        return remote;
    }

    public RemoteObject build(RemoteDefinition remote, ProcedureObject procedure) {
        RemoteProcessorMediator processorMediator = this.coreManager.create(RemoteProcessorMediator.class);
        for (IRemoteResolver remoteResolver : this.remoteResolvers) {
            remoteResolver.resolve(remote, processorMediator);
        }

        return this.create(processorMediator, remote, procedure);
    }

    private HandleTableObject createHandleTable(HandleTableDefinition definition, ProcedureObject procedure) {
        HandleTableObject handleTable = this.coreManager.create(HandleTableObject.class);

        handleTable.setBase(procedure);
        handleTable.setDefinition(definition);
        handleTable.factory = this;

        return handleTable;
    }

    public HandleTableObject buildHandleTable(HandleTableDefinition definition, ProcedureObject procedure) {
        return this.createHandleTable(definition, procedure);
    }
}
