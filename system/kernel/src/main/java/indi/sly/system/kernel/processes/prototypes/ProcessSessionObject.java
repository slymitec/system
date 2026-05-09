package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.core.prototypes.IByteValueProcess;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.values.InfoWildcardDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadComponentFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteComponentConsumer;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.*;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.*;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessSessionObject extends AChildCacheableObject<ProcessChildCacheEntity, ProcessObject> implements IByteValueProcess<ProcessSessionDefinition> {
    protected ProcessFactory factory;
    protected ProcessProcessorMediator processorMediator;

    private ProcessEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcess().getProcessId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getProcess().getProcessId());
    }

    private ProcessSessionDefinition init(ProcessEntity process) {
        Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessSessions();

        byte[] source = null;

        for (ProcessProcessorReadComponentFunction resolver : resolvers) {
            source = resolver.apply(source, process);
        }

        return IByteValueProcess.super.init(source);
    }

    private void flush(ProcessEntity process, ProcessSessionDefinition value) {
        byte[] source = IByteValueProcess.super.flush(value);

        Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessSessions();

        for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
            resolver.accept(process, source);
        }
    }

    public UUID getId() {
        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessSessionDefinition processSession = this.init(process);

            return processSession.getSessionID();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void create(String name, long type) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
        UserManager userManager = this.coreManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();
        UUID accountID = account.getId();

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessSessionDefinition processSession = this.init(process);

            UUID sessionID = processSession.getSessionID();

            if (processSession.isLink()) {
                throw new StatusRelationshipErrorException();
            }

            InfoObject sessionsInfo = objectManager.get(new PathDefinition(List.of(new IdentifierDefinition("Sessions"))));

            if (!ValueUtil.isAnyNullOrEmpty(sessionID)) {
                Set<InfoSummaryDefinition> infoSummary = sessionsInfo.queryChild(new InfoWildcardDefinition(sessionID));
                if (!infoSummary.isEmpty()) {
                    throw new StatusAlreadyExistedException();
                }
            }

            InfoObject sessionInfo = sessionsInfo.createChild(kernelConfiguration.PROCESSES_SESSION_INSTANCE_ID,
                    new IdentifierDefinition(UUID.randomUUID()));

            SecurityDescriptorObject securityDescriptor = sessionInfo.getSecurityDescriptor();
            Set<AccessControlDefinition> permissions = new HashSet<>();
            AccessControlDefinition permission = new AccessControlDefinition();
            permission.setUserId(new UserIDDefinition(accountID, UserType.ACCOUNT));
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW));
            permissions.add(permission);
            permission = new AccessControlDefinition();
            permission.setUserId(new UserIDDefinition(sessionID, UserType.SESSION));
            permission.setScope(AccessControlScopeType.THIS);
            permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW));
            permissions.add(permission);
            securityDescriptor.setPermissions(permissions);

            sessionsInfo.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);

            SessionContentObject sessionContent = (SessionContentObject) sessionInfo.getContent();
            sessionContent.setName(name);
            sessionContent.setType(type);

            sessionsInfo.close();

            ProcessInfoTableObject processInfoTable = this.base.getInfoTable();
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(sessionInfo.getId());
            processInfoEntry.setUnsupportedDelete(true);

            sessionID = sessionInfo.getId();

            processSession.setSessionID(sessionID);
            processSession.setLink(true);

            this.flush(process, processSession);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void close() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessSessionDefinition processSession = this.init(process);

            UUID sessionID = processSession.getSessionID();

            if (!processSession.isLink() || ValueUtil.isAnyNullOrEmpty(sessionID)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessInfoTableObject processInfoTable = this.base.getInfoTable();
            if (processInfoTable.containById(sessionID)) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(sessionID);
                processInfoEntry.setUnsupportedDelete(false);

                InfoObject info = processInfoEntry.getInfo();
                info.close();
            }

            processSession.setSessionID(null);
            processSession.setLink(false);

            this.flush(process, processSession);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void inheritID() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        if (!currentProcess.getId().equals(base.getParentId())) {
            throw new ConditionRefuseException();
        }

        ProcessSessionObject currentProcessSession = currentProcess.getSession();
        UUID sessionID = currentProcessSession.getId();

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessSessionDefinition processSession = this.init(process);

            if (processSession.isLink()) {
                throw new StatusRelationshipErrorException();
            }

            processSession.setSessionID(sessionID);
            processSession.setLink(false);

            this.flush(process, processSession);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void link() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessSessionDefinition processSession = this.init(process);

            UUID sessionID = processSession.getSessionID();

            if (processSession.isLink()) {
                throw new StatusAlreadyFinishedException();
            }
            if (ValueUtil.isAnyNullOrEmpty(sessionID)) {
                throw new StatusRelationshipErrorException();
            }

            InfoObject sessionInfo = objectManager.get(new PathDefinition(List.of(new IdentifierDefinition("Sessions"),
                    new IdentifierDefinition(sessionID))));
            sessionInfo.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);

            ProcessInfoTableObject processInfoTable = this.base.getInfoTable();
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(sessionInfo.getId());
            processInfoEntry.setUnsupportedDelete(true);

            processSession.setLink(true);

            this.flush(process, processSession);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public boolean isLink() {
        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessSessionDefinition processSession = this.init(process);

            return processSession.isLink();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public SessionContentObject getContent() {
        if (!this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessSessionDefinition processSession = this.init(process);

            UUID sessionID = processSession.getSessionID();

            if (!processSession.isLink() || ValueUtil.isAnyNullOrEmpty(sessionID)) {
                throw new StatusRelationshipErrorException();
            }

            ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);

            PathDefinition path = new PathDefinition(List.of(new IdentifierDefinition("Sessions"),
                    new IdentifierDefinition(sessionID)));

            InfoObject sessionInfo = objectManager.get(path);

            SessionContentObject sessionContent;
            try {
                sessionContent = (SessionContentObject) sessionInfo.getContent();
            } catch (AKernelException ignored) {
                sessionContent = null;
            }

            return sessionContent;
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }
}
