package indi.sly.system.kernel.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoStatusOpenAttributeType;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.values.PrivilegeTypes;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.instances.prototypes.wrappers.SessionTypeInitializer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionManager extends AManager {
    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupType.STEP_INIT) {
        } else if (startupTypes == StartupType.STEP_KERNEL) {
            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);

            KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

            Set<UUID> childTypes = Set.of(UUIDUtil.getEmpty());

            typeManager.create(kernelConfiguration.PROCESSES_CONTEXT_INSTANCE_SESSION_ID,
                    kernelConfiguration.PROCESSES_CONTEXT_INSTANCE_SESSION_NAME,
                    LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SENT_AND_INHERITED,
                            TypeInitializerAttributeType.CAN_BE_SHARED_READ, TypeInitializerAttributeType.HAS_AUDIT,
                            TypeInitializerAttributeType.HAS_CONTENT, TypeInitializerAttributeType.HAS_PERMISSION,
                            TypeInitializerAttributeType.HAS_PROPERTIES),
                    childTypes, this.factoryManager.create(SessionTypeInitializer.class));
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void check() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.SESSION_MODIFY_USERSESSION)) {
            throw new ConditionPermissionsException();
        }
    }

    public SessionContentObject getAndOpen(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Sessions"),
                new IdentificationDefinition(id));

        InfoObject session = objectManager.get(identifications);
        session.open(InfoStatusOpenAttributeType.OPEN_SHARED_WRITE);

        return (SessionContentObject) session.getContent();
    }

    public void close(SessionContentObject sessionContent) {
        if (ObjectUtil.isAnyNull(sessionContent)) {
            throw new ConditionParametersException();
        }

        sessionContent.close();
    }

    public UUID create(AccountAuthorizationObject accountAuthorization) {
        if (ObjectUtil.isAnyNull(accountAuthorization)) {
            throw new ConditionParametersException();
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Sessions"));

        InfoObject sessions = objectManager.get(identifications);

        UUID typeID = this.factoryManager.getKernelSpace().getConfiguration().PROCESSES_CONTEXT_INSTANCE_SESSION_ID;
        UUID accountID = accountAuthorization.checkAndGetResult().getAccountID();

        InfoObject session = sessions.createChildAndOpen(typeID, new IdentificationDefinition(UUIDUtil.createRandom()),
                InfoStatusOpenAttributeType.OPEN_SHARED_WRITE);
        session.close();

        return session.getID();
    }

    public void delete(UUID id) {
        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Sessions"));

        InfoObject sessions = objectManager.get(identifications);

        sessions.deleteChild(new IdentificationDefinition(id));
    }
}
