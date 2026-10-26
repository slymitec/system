package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AFactory;
import indi.sly.subsystem.periphery.proxies.instances.IProxy;
import indi.sly.subsystem.periphery.proxies.instances.core.CoreProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.core.ICoreProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.core.IDateTimeProxy;
import indi.sly.subsystem.periphery.proxies.instances.core.ISystemVersionProxy;
import indi.sly.subsystem.periphery.proxies.instances.core.prototypes.DateTimeProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.core.prototypes.SystemVersionProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.objects.ObjectProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.DumpProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.InfoProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.SecurityDescriptorProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.instances.FolderContentProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.instances.NamelessFolderContentProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.processes.ProcessProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.processes.prototypes.*;
import indi.sly.subsystem.periphery.proxies.instances.security.*;
import indi.sly.subsystem.periphery.proxies.instances.security.instances.IAuditContentProxy;
import indi.sly.subsystem.periphery.proxies.instances.security.prototypes.*;
import indi.sly.subsystem.periphery.proxies.instances.security.prototypes.instances.AuditContentProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.services.ServicesProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.services.prototypes.ServiceContentProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.prototypes.processors.IRemoteResolver;
import indi.sly.subsystem.periphery.proxies.prototypes.processors.RemoteCallResolver;
import indi.sly.subsystem.periphery.proxies.prototypes.processors.RemoteCheckConditionResolver;
import indi.sly.subsystem.periphery.proxies.prototypes.processors.RemoteDieResolver;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.subsystem.periphery.proxies.values.SessionDefinition;
import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import io.dapr.actors.ActorId;
import io.dapr.actors.client.ActorClient;
import io.dapr.actors.client.ActorProxyBuilder;
import jakarta.inject.Named;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyFactory extends AFactory {
    public ProxyFactory() {
        this.remoteResolvers = new CopyOnWriteArrayList<>();
        this.proxyTasks = new ConcurrentHashMap<>();
        this.proxyObjectTasks = new ConcurrentHashMap<>();
        this.systemExceptions = new ConcurrentHashMap<>();
    }

    private final List<IRemoteResolver> remoteResolvers;
    private final Map<Class<? extends IProxy>, String> proxyTasks;
    private final Map<Class<? extends AProxyObject>, String> proxyObjectTasks;
    private final Map<String, ASystemException> systemExceptions;

    @Override
    public void init() {
        this.remoteResolvers.add(this.coreManager.create(RemoteCallResolver.class));
        this.remoteResolvers.add(this.coreManager.create(RemoteCheckConditionResolver.class));
        this.remoteResolvers.add(this.coreManager.create(RemoteDieResolver.class));
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

        this.registerProxy(ICoreProxyManager.class, "CoreManager");
        this.registerProxy(IDateTimeProxy.class, "DateTimeObject");
        this.registerProxy(ISystemVersionProxy.class, "SystemVersionObject");

        this.registerProxyObject(CoreProxyManager.class, "CoreManager");
        this.registerProxyObject(SystemVersionProxyObject.class, "SystemVersionObject");
        this.registerProxyObject(DateTimeProxyObject.class, "DateTimeObject");

        this.registerProxyObject(ObjectProxyManager.class, "ObjectManager");
        this.registerProxyObject(DumpProxyObject.class, "DumpObject");
        this.registerProxyObject(InfoProxyObject.class, "InfoObject");
        this.registerProxyObject(SecurityDescriptorProxyObject.class, "SecurityDescriptorObject");
        this.registerProxyObject(FolderContentProxyObject.class, "FolderContentObject");
        this.registerProxyObject(NamelessFolderContentProxyObject.class, "NamelessFolderContentObject");

        this.registerProxyObject(ProcessProxyManager.class, "ProcessManager");
        this.registerProxyObject(ProcessProxyObject.class, "ProcessObject");
        this.registerProxyObject(ProcessCommunicationProxyObject.class, "ProcessCommunicationObject");
        this.registerProxyObject(ProcessContextProxyObject.class, "ProcessContextObject");
        this.registerProxyObject(ProcessInfoEntryProxyObject.class, "ProcessInfoEntryObject");
        this.registerProxyObject(ProcessInfoTableProxyObject.class, "ProcessInfoTableObject");
        this.registerProxyObject(ProcessSessionProxyObject.class, "ProcessSessionObject");
        this.registerProxyObject(ProcessStatisticsProxyObject.class, "ProcessStatisticsObject");
        this.registerProxyObject(ProcessStatusProxyObject.class, "ProcessStatusObject");
        this.registerProxyObject(ProcessTokenProxyObject.class, "ProcessTokenObject");

        this.registerProxy(IUserProxyManager.class, "UserManager");
        this.registerProxy(IAccountAuthorizationProxy.class, "AccountAuthorizationObject");
        this.registerProxy(IAccountProxy.class, "AccountObject");
        this.registerProxy(IAccountSessionsProxy.class, "AccountSessionsObject");
        this.registerProxy(IAccountTokenProxy.class, "AccountTokenObject");
        this.registerProxy(IGroupProxy.class, "GroupObject");
        this.registerProxy(IGroupTokenProxy.class, "GroupTokenObject");
        this.registerProxy(IAuditContentProxy.class, "AuditContentObject");

        this.registerProxyObject(UserProxyManager.class, "UserManager");
        this.registerProxyObject(AccountAuthorizationProxyObject.class, "AccountAuthorizationObject");
        this.registerProxyObject(AccountProxyObject.class, "AccountObject");
        this.registerProxyObject(AccountSessionsProxyObject.class, "AccountSessionsObject");
        this.registerProxyObject(AccountTokenProxyObject.class, "AccountTokenObject");
        this.registerProxyObject(GroupProxyObject.class, "GroupObject");
        this.registerProxyObject(GroupTokenProxyObject.class, "GroupTokenObject");
        this.registerProxyObject(AuditContentProxyObject.class, "AuditContentObject");

        this.registerProxyObject(ServicesProxyManager.class, "ServicesManager");
        this.registerProxyObject(ServiceContentProxyObject.class, "ServiceContentObject");
    }

    private void registerProxyObject(Class<? extends AProxyObject> clazz, String taskName) {
        this.proxyObjectTasks.put(clazz, taskName);
    }

    private void registerProxy(Class<? extends IProxy> clazz, String taskName) {
        this.proxyTasks.put(clazz, taskName);
    }

    public IKernelObjectActor getKernelObjectActor(String taskName, UUID handle) {
        ActorClient actorClient = SpringHelper.getInstance(ActorClient.class);

        IKernelObjectActor kernelObjectActor = new ActorProxyBuilder<>(IKernelObjectActor.class, actorClient).build(new ActorId(taskName + "|" + UUIDUtil.toString(handle)));

        return kernelObjectActor;
    }

    public ASystemException getSystemException(String systemExceptionName) {
        return this.systemExceptions.get(systemExceptionName);
    }

    public String acquireTaskName(Class<? extends IProxy> clazz) {
        return this.proxyTasks.get(clazz);
    }

    public String acquireObjectTaskName(Class<? extends AProxyObject> clazz) {
        return this.proxyObjectTasks.get(clazz);
    }

    public boolean isTaskExist(String taskName) {
        return this.proxyObjectTasks.containsValue(taskName);
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

    private RemoteObject[] splitProxyRemote(RemoteObject remote) {
        RemoteDefinition definition = remote.getDefinition();

        UUID[] handles = ObjectUtil.transferFromString(UUID[].class, definition.getValue());

        if (ObjectUtil.isNull(handles)) {
            throw new StatusRelationshipErrorException();
        }

        int handleLength = handles.length;
        RemoteObject[] remotes = new RemoteObject[handleLength];

        for (int i = 0; i < handleLength; i++) {
            RemoteDefinition newDefinition = new RemoteDefinition();
            newDefinition.setCallContext(definition.getCallContext());
            newDefinition.setValue(ObjectUtil.transferToString(handles[i]));

            remotes[i] = this.buildRemote(newDefinition);
        }

        return remotes;
    }

    @SuppressWarnings("unchecked")
    private <T extends IProxy> T createProxy(Class<T> clazz, RemoteObject remote) {
        ProxyComponent proxyComponent = this.coreManager.create(ProxyComponent.class);

        proxyComponent.factory = this;
        proxyComponent.remote = remote;

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, proxyComponent);
    }

    public <T extends IProxy> T buildProxy(Class<T> clazz, RemoteObject remote) {
        if (ValueUtil.isNullOrEmpty(remote.getValue())) {
            throw new StatusRelationshipErrorException();
        }

        String taskName = this.acquireTaskName(clazz);
        remote.getDefinition().setTaskName(taskName);

        return this.createProxy(clazz, remote);
    }

    public <T extends IProxy> Set<T> buildProxySet(Class<T> clazz, RemoteObject remote) {
        if (ValueUtil.isNullOrEmpty(remote.getValue())) {
            throw new StatusRelationshipErrorException();
        }

        RemoteObject[] remotes = this.splitProxyRemote(remote);

        Set<T> proxySet = new HashSet<>();

        for (RemoteObject remotePair : remotes) {
            proxySet.add(this.buildProxy(clazz, remotePair));
        }

        return proxySet;
    }

    public <T extends IProxy> List<T> buildProxyList(Class<T> clazz, RemoteObject remote) {
        if (ValueUtil.isNullOrEmpty(remote.getValue())) {
            throw new StatusRelationshipErrorException();
        }

        RemoteObject[] remotes = this.splitProxyRemote(remote);

        List<T> proxySet = new ArrayList<>();

        for (RemoteObject remotePair : remotes) {
            proxySet.add(this.buildProxy(clazz, remotePair));
        }

        return proxySet;
    }

    private <T extends AProxyObject> T createProxyObject(Class<T> clazz, RemoteObject remote) {
        T proxy = this.coreManager.create(clazz);

        proxy.factory = this;
        proxy.remote = remote;

        return proxy;
    }

    public <T extends AProxyObject> T buildProxyObject(Class<T> clazz, RemoteObject remote) {
        if (ValueUtil.isNullOrEmpty(remote.getValue())) {
            throw new StatusRelationshipErrorException();
        }

        String taskName = this.acquireObjectTaskName(clazz);
        remote.getDefinition().setTaskName(taskName);

        return this.createProxyObject(clazz, remote);
    }

    public <T extends AProxyObject> Set<T> buildProxyObjectSet(Class<T> clazz, RemoteObject remote) {
        if (ValueUtil.isNullOrEmpty(remote.getValue())) {
            throw new StatusRelationshipErrorException();
        }

        RemoteObject[] remotes = this.splitProxyRemote(remote);

        Set<T> proxySet = new HashSet<>();

        for (RemoteObject remotePair : remotes) {
            proxySet.add(this.buildProxyObject(clazz, remotePair));
        }

        return proxySet;
    }

    public <T extends AProxyObject> List<T> buildProxyObjectList(Class<T> clazz, RemoteObject remote) {
        if (ValueUtil.isNullOrEmpty(remote.getValue())) {
            throw new StatusRelationshipErrorException();
        }

        RemoteObject[] remotes = this.splitProxyRemote(remote);

        List<T> proxySet = new ArrayList<>();

        for (RemoteObject remotePair : remotes) {
            proxySet.add(this.buildProxyObject(clazz, remotePair));
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

    public <T> @Nullable T getValue(Class<T> returnClass, RemoteObject remote) {
        return ObjectUtil.transferFromString(returnClass, remote.getValue());
    }

    public <T> @Nullable Set<T> getSetValue(Class<T> returnClass, RemoteObject remote) {
        return ObjectUtil.transferSetFromString(returnClass, remote.getValue());
    }

    public <T> @Nullable List<T> getListValue(Class<T> returnClass, RemoteObject remote) {
        return ObjectUtil.transferListFromString(returnClass, remote.getValue());
    }

    public @Nullable <TK, TV> Map<TK, TV> getMapValue(Class<TK> returnKeyClass, Class<TV> returnValueClass, RemoteObject remote) {
        return ObjectUtil.transferMapFromString(returnKeyClass, returnValueClass, remote.getValue());
    }
}
