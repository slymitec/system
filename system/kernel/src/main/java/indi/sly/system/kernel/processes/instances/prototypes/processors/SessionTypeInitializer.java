package indi.sly.system.kernel.processes.instances.prototypes.processors;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.instances.values.SessionDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionTypeInitializer extends AInfoTypeInitializer {
    @Override
    public UUID getPoolID(UUID id, UUID type) {
        return this.factoryManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID;
    }

    @Override
    public void createProcedure(InfoEntity info) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        SessionDefinition session = new SessionDefinition();

        session.setAccountID(processToken.getAccountID());

        info.setContent(ObjectUtil.transferToByteArray(session));
    }

    @Override
    public void openProcedure(InfoEntity info, InfoOpenDefinition infoOpen, Object[] arguments) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        SessionDefinition session = new SessionDefinition();

        session.setAccountID(processToken.getAccountID());
        session.getProcessIDs().add(process.getID());
    }

    @Override
    public void closeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        SessionDefinition session = new SessionDefinition();

        session.setAccountID(processToken.getAccountID());
        session.getProcessIDs().remove(process.getID());
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        return SessionContentObject.class;
    }
}
