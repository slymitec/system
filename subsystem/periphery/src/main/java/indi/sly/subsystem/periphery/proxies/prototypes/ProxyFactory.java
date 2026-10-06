package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AFactory;
import indi.sly.subsystem.periphery.proxies.instances.core.CoreProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.core.prototypes.DateTimeProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.core.prototypes.SystemVersionProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.objects.ObjectProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.*;
import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.instances.FolderContentProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.instances.NamelessFolderContentProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.processes.ProcessProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.processes.prototypes.*;
import indi.sly.subsystem.periphery.proxies.instances.security.UserProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.security.prototypes.*;
import indi.sly.subsystem.periphery.proxies.instances.security.prototypes.instances.AuditContentProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.services.ServicesProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.services.prototypes.ServiceContentProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.prototypes.processors.*;
import indi.sly.subsystem.periphery.proxies.values.*;
import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import io.dapr.actors.ActorId;
import io.dapr.actors.client.ActorClient;
import io.dapr.actors.client.ActorProxyBuilder;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;
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

        this.registerProxy(ObjectProxyManager.class, "ObjectManager");
        this.registerProxy(DumpProxyObject.class, "DumpObject");
        this.registerProxy(InfoProxyObject.class, "InfoObject");
        this.registerProxy(SecurityDescriptorProxyObject.class, "SecurityDescriptorObject");
        this.registerProxy(FolderContentProxyObject.class, "FolderContentObject");
        this.registerProxy(NamelessFolderContentProxyObject.class, "NamelessFolderContentObject");

        this.registerProxy(ProcessProxyManager.class, "ProcessManager");
        this.registerProxy(ProcessProxyObject.class, "ProcessObject");
        this.registerProxy(ProcessCommunicationProxyObject.class, "ProcessCommunicationObject");
        this.registerProxy(ProcessContextProxyObject.class, "ProcessContextObject");
        this.registerProxy(ProcessInfoEntryProxyObject.class, "ProcessInfoEntryObject");
        this.registerProxy(ProcessInfoTableProxyObject.class, "ProcessInfoTableObject");
        this.registerProxy(ProcessSessionProxyObject.class, "ProcessSessionObject");
        this.registerProxy(ProcessStatisticsProxyObject.class, "ProcessStatisticsObject");
        this.registerProxy(ProcessStatusProxyObject.class, "ProcessStatusObject");
        this.registerProxy(ProcessTokenProxyObject.class, "ProcessTokenObject");

        this.registerProxy(UserProxyManager.class, "UserManager");
        this.registerProxy(AccountAuthorizationProxyObject.class, "AccountAuthorizationObject");
        this.registerProxy(AccountProxyObject.class, "AccountObject");
        this.registerProxy(AccountSessionsProxyObject.class, "AccountSessionsObject");
        this.registerProxy(AccountTokenProxyObject.class, "AccountTokenObject");
        this.registerProxy(GroupProxyObject.class, "GroupObject");
        this.registerProxy(GroupTokenProxyObject.class, "GroupTokenObject");
        this.registerProxy(AuditContentProxyObject.class, "AuditContentObject");

        this.registerProxy(ServicesProxyManager.class, "ServicesManager");
        this.registerProxy(ServiceContentProxyObject.class, "ServiceContentObject");
    }

    private void registerProxy(Class<? extends AProxyObject> clazz, String taskName) {
        this.proxyObjectTasks.put(clazz, taskName);
    }

    public IKernelObjectActor getKernelObjectActor(String taskName, UUID handle) {
        ActorClient actorClient = SpringHelper.getInstance(ActorClient.class);

        IKernelObjectActor kernelObjectActor = new ActorProxyBuilder<>(IKernelObjectActor.class, actorClient).build(new ActorId(taskName + "|" + UUIDUtil.toString(handle)));

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

    private RemoteObject[] splitRemote(RemoteObject remote) {
        RemoteDefinition definition = remote.getDefinition();

        String[] definitionValues = ObjectUtil.transferFromString(String[].class, definition.getValue());
        int definitionValuesLength = definitionValues.length;
        RemoteObject[] remotes = new RemoteObject[definitionValuesLength];

        for (int i = 0; i < definitionValues.length; i++) {
            RemoteDefinition newDefinition = new RemoteDefinition();
            newDefinition.setCallContext(definition.getCallContext());
            newDefinition.setValue(definitionValues[i]);

            remotes[i] = this.buildRemote(newDefinition);
        }

        return remotes;
    }

    private <T extends AProxyObject> T createProxy(Class<T> clazz, RemoteObject remote) {
        T proxy = this.coreManager.create(clazz);

        proxy.factory = this;
        proxy.remote = remote;
        remote.setProxyClass(clazz);

        return proxy;
    }

    public <T extends AProxyObject> T buildProxy(Class<T> clazz, RemoteObject remote) {
        if (ValueUtil.isAnyNullOrEmpty(remote.getValue())) {
            throw new StatusRelationshipErrorException();
        }

        return this.createProxy(clazz, remote);
    }

    public <T extends AProxyObject> Set<T> buildProxySet(Class<T> clazz, RemoteObject remote) {
        if (ValueUtil.isAnyNullOrEmpty(remote.getValue())) {
            throw new StatusRelationshipErrorException();
        }

        RemoteObject[] remotes = this.splitRemote(remote);

        Set<T> proxySet = new HashSet<>();

        for (RemoteObject remotePair : remotes) {
            proxySet.add(this.buildProxy(clazz, remotePair));
        }

        return proxySet;
    }

    public <T extends AProxyObject> List<T> buildProxyList(Class<T> clazz, RemoteObject remote) {
        if (ValueUtil.isAnyNullOrEmpty(remote.getValue())) {
            throw new StatusRelationshipErrorException();
        }

        RemoteObject[] remotes = this.splitRemote(remote);

        List<T> proxySet = new ArrayList<>();

        for (RemoteObject remotePair : remotes) {
            proxySet.add(this.buildProxy(clazz, remotePair));
        }

        return proxySet;
    }

    private SessionObject createSession(SessionDefinition definition) {
        SessionObject session = this.coreManager.create(SessionObject.class);

        session.factory = this;
        session.setDefinition(definition);

        return session;
    }

    public SessionObject buildSession(UUID sessionId) {
        SessionDefinition session = new SessionDefinition();

        session.setSessionId(sessionId);

        return this.createSession(session);
    }

    public <T> T getValue(Class<T> returnClass, RemoteObject remote) {
        return ObjectUtil.transferFromString(returnClass, remote.getValue());
    }

    public <T> Set<T> getSetValue(Class<T> returnClass, RemoteObject remote) {
        return ObjectUtil.transferSetFromString(returnClass, remote.getValue());
    }

    public <T> List<T> getListValue(Class<T> returnClass, RemoteObject remote) {
        return ObjectUtil.transferListFromString(returnClass, remote.getValue());
    }

    public <TK, TV> Map<TK, TV> getMapValue(Class<TK> returnKeyClass, Class<TV> returnValueClass, RemoteObject remote) {
        return ObjectUtil.transferMapFromString(returnKeyClass, returnValueClass, remote.getValue());
    }
}
