package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateCheckResolver extends APrototype implements IProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateCheckResolver() {
        this.create = (process, parentProcess, processCreator) -> {
            ProcessTokenObject parentProcessToken = parentProcess.getToken();

            Map<Long, Integer> limits = processCreator.getLimits();
            if (ObjectUtil.allNotNull(limits) && !limits.isEmpty() && !parentProcessToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
                throw new ConditionPermissionsException();
            }

            Set<UUID> additionalRoles = processCreator.getAdditionalRoles();
            if (ObjectUtil.allNotNull(additionalRoles) && !additionalRoles.isEmpty() && !parentProcessToken.isPrivileges(PrivilegeType.PROCESSES_ADD_ROLES)) {
                throw new ConditionPermissionsException();
            }

            if (processCreator.getPrivileges() != PrivilegeType.NULL && !parentProcessToken.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionPermissionsException();
            }

            if (!ValueUtil.isAnyNullOrEmpty(processCreator.getSessionID()) && !parentProcessToken.isPrivileges(PrivilegeType.SESSION_MODIFY_USER_SESSION)) {
                throw new ConditionPermissionsException();
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(create);
    }
}