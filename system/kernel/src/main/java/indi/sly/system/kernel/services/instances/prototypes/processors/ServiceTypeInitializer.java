package indi.sly.system.kernel.services.instances.prototypes.processors;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoOpenRecord;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import indi.sly.system.kernel.services.instances.prototypes.ServiceContentObject;
import indi.sly.system.kernel.services.instances.values.ServiceDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceTypeInitializer extends AInfoTypeInitializer {
    @Override
    public UUID getPoolId(UUID id, UUID type) {
        return this.coreManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORY_ID;
    }

    @Override
    public void createProcedure(InfoEntity info) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = new ServiceDefinition();

        info.setContent(ObjectUtil.transferToByteArray(service));
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenRecord infoOpen) {
        return ServiceContentObject.class;
    }
}
