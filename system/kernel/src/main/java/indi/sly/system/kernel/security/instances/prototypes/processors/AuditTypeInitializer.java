package indi.sly.system.kernel.security.instances.prototypes.processors;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoOpenRecord;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.instances.prototypes.AuditContentObject;
import indi.sly.system.kernel.security.instances.values.AuditDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuditTypeInitializer extends AInfoTypeInitializer {
    @Override
    public UUID getPoolId(UUID id, UUID type) {
        return this.coreManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORY_ID;
    }

    @Override
    public void createProcedure(InfoEntity info) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        AuditDefinition audit = new AuditDefinition();

        audit.setProcessId(process.getId());
        audit.setAccountId(processToken.getAccountId());

        info.setContent(ObjectUtil.transferToByteArray(audit));
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenRecord infoOpen) {
        return AuditContentObject.class;
    }
}
