package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AFactory;
import indi.sly.subsystem.periphery.proxies.instances.core.CoreManagerProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.core.DateTimeProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.prototypes.processors.*;
import indi.sly.subsystem.periphery.proxies.values.*;
import indi.sly.system.common.supports.CollectionUtil;
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
        this.proxyManagers = new ConcurrentHashMap<>();
        this.proxyObjects = new ConcurrentHashMap<>();
    }

    private final List<IRemoteResolver> remoteResolvers;
    private final Map<Class<? extends AProxyObject>, RemoteDefinition> proxyManagers;
    private final Map<String, Class<? extends AProxyObject>> proxyObjects;

    @Override
    public void init() {
        this.remoteResolvers.add(this.coreManager.create(RemoteCallResolver.class));
        this.remoteResolvers.add(this.coreManager.create(RemoteCheckConditionResolver.class));
        this.remoteResolvers.add(this.coreManager.create(RemoteCheckExpiredResolver.class));
        this.remoteResolvers.add(this.coreManager.create(RemoteDateResolver.class));

        Collections.sort(this.remoteResolvers);

        this.createProxyManager(CoreManagerProxyObject.class, "CoreManager");

        this.proxyObjects.put("DateTime", DateTimeProxyObject.class);
    }

    private void createProxyManager(Class<? extends AProxyObject> clazz, String clazzName) {
        RemoteDefinition remote = new RemoteDefinition();
        remote.setType(RemoteTypes.MANAGER);
        remote.setClazz(clazzName);
        this.proxyManagers.put(clazz, remote);
    }

    public Map<Class<? extends AProxyObject>, RemoteDefinition> getProxyManagers() {
        return CollectionUtil.unmodifiable(this.proxyManagers);
    }

    public Map<String, Class<? extends AProxyObject>> getProxyObjects() {
        return CollectionUtil.unmodifiable(this.proxyObjects);
    }

    private RemoteObject createRemote(RemoteProcessorMediator processorMediator, RemoteDefinition definition, ProcedureObject procedure) {
        RemoteObject remote = this.coreManager.create(RemoteObject.class);

        remote.setBase(procedure);
        remote.setDefinition(definition);
        remote.factory = this;
        remote.processorMediator = processorMediator;

        return remote;
    }

    public RemoteObject buildRemote(RemoteDefinition remote, ProcedureObject procedure) {
        RemoteProcessorMediator processorMediator = this.coreManager.create(RemoteProcessorMediator.class);
        for (IRemoteResolver remoteResolver : this.remoteResolvers) {
            remoteResolver.resolve(remote, processorMediator);
        }

        return this.createRemote(processorMediator, remote, procedure);
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

    private ProcedureObject createProcedure(ProcedureDefinition definition) {
        ProcedureObject procedure = this.coreManager.create(ProcedureObject.class);

        procedure.setDefinition(definition);
        procedure.factory = this;

        return procedure;
    }

    public ProcedureObject buildProcedure(String call, ProcedureProcessRecord process) {
        ProcedureDefinition procedure = new ProcedureDefinition();

        procedure.setCall(call);
        procedure.setProcess(process);

        return this.createProcedure(procedure);
    }

    public AProxyObject buildProxy(Class<? extends AProxyObject> clazz, RemoteObject remote){
        AProxyObject proxy = this.coreManager.create(clazz);

        proxy.factory = this;
        proxy.setRemote(remote);

        return proxy;
    }
}
