package indi.sly.system.kernel.processes.instances.prototypes.processors;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.instances.values.SessionDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessSessionObject;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.prototypes.AccountSessionsObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

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
        UserManager userManager = this.factoryManager.getManager(UserManager.class);
        AccountObject account = userManager.getCurrentAccount();

        SessionDefinition session = new SessionDefinition();
        session.setAccountID(account.getID());
        info.setContent(ObjectUtil.transferToByteArray(session));

        AccountSessionsObject accountSessions = account.getSessions();
        accountSessions.addSession(info.getID());
    }

    @Override
    public void deleteProcedure(InfoEntity info) {
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        SessionDefinition session = ObjectUtil.transferFromByteArray(info.getContent());
        AccountObject account = userManager.getAccount(session.getAccountID());

        AccountSessionsObject accountSessions = account.getSessions();
        accountSessions.deleteSession(info.getID());
    }

    @Override
    public void openProcedure(InfoEntity info, InfoOpenDefinition infoOpen, Object[] arguments) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        ProcessSessionObject processSession = process.getSession();
        if (!info.getID().equals(processSession.getID())) {
            throw new StatusRelationshipErrorException();
        }

        SessionDefinition session = ObjectUtil.transferFromByteArray(info.getContent());
        session.getProcessIDs().add(process.getID());
        info.setContent(ObjectUtil.transferToByteArray(session));
    }

    @Override
    public void closeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        ProcessSessionObject processSession = process.getSession();
        if (!info.getID().equals(processSession.getID())) {
            throw new StatusRelationshipErrorException();
        }

        SessionDefinition session = ObjectUtil.transferFromByteArray(info.getContent());
        session.getProcessIDs().remove(process.getID());
        info.setContent(ObjectUtil.transferToByteArray(session));
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        return SessionContentObject.class;
    }
}
