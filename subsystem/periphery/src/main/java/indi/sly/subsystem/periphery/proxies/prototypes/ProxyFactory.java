package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AFactory;
import indi.sly.subsystem.periphery.proxies.instances.core.CoreProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.core.DateTimeProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.core.SystemVersionProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.prototypes.processors.*;
import indi.sly.subsystem.periphery.proxies.values.*;
import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.common.supports.UUIDUtil;
import io.dapr.actors.ActorId;
import io.dapr.actors.client.ActorClient;
import io.dapr.actors.client.ActorProxyBuilder;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyFactory extends AFactory {
    public ProxyFactory() {
        this.remoteResolvers = new CopyOnWriteArrayList<>();
        this.proxyObjectTasks = new ConcurrentHashMap<>();
        this.systemExceptions = new ConcurrentHashMap<>();
    }

    private final List<IRemoteResolver> remoteResolvers;
    private final Map<Class<? extends AProxyObject>, String> proxyObjectTasks;
    private final Map<String, ASystemException> systemExceptions;

    @Override
    public void init() {
        this.remoteResolvers.add(this.coreManager.create(RemoteCallResolver.class));
        this.remoteResolvers.add(this.coreManager.create(RemoteCheckConditionResolver.class));
        this.remoteResolvers.add(this.coreManager.create(RemoteDateResolver.class));
        Collections.sort(this.remoteResolvers);

        this.systemExceptions.put("StatusRelationshipErrorException", new StatusRelationshipErrorException());
        this.systemExceptions.put("StatusNotReadyException", new StatusNotReadyException());
        this.systemExceptions.put("StatusInsufficientResourcesException", new StatusInsufficientResourcesException());
        this.systemExceptions.put("StatusAlreadyExistedException", new StatusAlreadyExistedException());
        this.systemExceptions.put("StatusIsUsedException", new StatusIsUsedException());
        this.systemExceptions.put("StatusUnreadableException", new StatusUnreadableException());
        this.systemExceptions.put("ConditionRefuseException", new ConditionRefuseException());
        this.systemExceptions.put("StatusNotExistedException", new StatusNotExistedException());
        this.systemExceptions.put("StatusUnexpectedException", new StatusUnexpectedException());
        this.systemExceptions.put("ConditionPermissionException", new ConditionPermissionException());
        this.systemExceptions.put("StatusExpiredException", new StatusExpiredException());
        this.systemExceptions.put("StatusAlreadyFinishedException", new StatusAlreadyFinishedException());
        this.systemExceptions.put("StatusNotSupportedException", new StatusNotSupportedException());
        this.systemExceptions.put("StatusNotWritableException", new StatusNotWritableException());
        this.systemExceptions.put("StatusOverflowException", new StatusOverflowException());
        this.systemExceptions.put("ConditionAuditException", new ConditionAuditException());
        this.systemExceptions.put("StatusDisabilityException", new StatusDisabilityException());
        this.systemExceptions.put("ConditionParametersException", new ConditionParametersException());
        this.systemExceptions.put("ConditionContextException", new ConditionContextException());

        this.registerProxy(CoreProxyManager.class, "CoreManager");
        this.registerProxy(SystemVersionProxyObject.class, "SystemVersionObject");
        this.registerProxy(DateTimeProxyObject.class, "DateTimeObject");
    }

    private void registerProxy(Class<? extends AProxyObject> clazz, String taskName) {
        this.proxyObjectTasks.put(clazz, taskName);
    }

    public IKernelObjectActor getKernelObjectActor(String taskName, UUID handle) {
        ActorClient actorClient = SpringHelper.getInstance(ActorClient.class);

        IKernelObjectActor kernelObjectActor = new ActorProxyBuilder<>(IKernelObjectActor.class, actorClient).build(new ActorId(taskName + "\\" + UUIDUtil.toString(handle)));

        return kernelObjectActor;
    }

    public ASystemException getSystemException(String systemExceptionName) {
        return this.systemExceptions.getOrDefault(systemExceptionName, null);
    }

    public String acquireTaskName(Class<? extends AProxyObject> clazz) {
        return this.proxyObjectTasks.getOrDefault(clazz, null);
    }

    private RemoteObject createRemote(RemoteProcessorMediator processorMediator, RemoteDefinition definition) {
        RemoteObject remote = this.coreManager.create(RemoteObject.class);

        remote.setDefinition(definition);
        remote.factory = this;
        remote.processorMediator = processorMediator;

        return remote;
    }

    public RemoteObject buildRemote(RemoteDefinition remote) {
        RemoteProcessorMediator processorMediator = this.coreManager.create(RemoteProcessorMediator.class);
        for (IRemoteResolver remoteResolver : this.remoteResolvers) {
            remoteResolver.resolve(remote, processorMediator);
        }

        return this.createRemote(processorMediator, remote);
    }

    private <T extends AProxyObject> T createProxy(Class<T> clazz, RemoteObject remote) {
        T proxy = this.coreManager.create(clazz);

        proxy.factory = this;
        proxy.remote = remote;

        return proxy;
    }

    public <T extends AProxyObject> T buildProxy(Class<T> clazz, RemoteObject remote) {
        return this.createProxy(clazz, remote);
    }
}
