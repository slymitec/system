package indi.sly.system.kernel.sessions;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.lang.OpenAttirbute;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoStatusOpenAttributeType;
import indi.sly.system.kernel.sessions.instances.prototypes.SessionContentObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionManager extends AManager {
    @Override
    public void startup(long startupTypes) {
    }

    @Override
    public void shutdown() {
    }

    public SessionContentObject getAndOpen(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"), new IdentificationDefinition(id));

        InfoObject session = objectManager.get(identifications);
        session.open(InfoStatusOpenAttributeType.OPEN_EXCLUSIVE);

        return (SessionContentObject) session.getContent();
    }

    public void close(SessionContentObject sessionContent) {
        if (ObjectUtil.isAnyNull(sessionContent)) {
            throw new ConditionParametersException();
        }

        sessionContent.close();
    }

    public UUID create() {
        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"));

        InfoObject sessions = objectManager.get(identifications);

        UUID typeID =
                this.factoryManager.getKernelSpace().getConfiguration().PROCESSES_COMMUNICATION_INSTANCE_SESSION_ID;

        InfoObject session = sessions.createChildAndOpen(typeID, new IdentificationDefinition(UUID.randomUUID()),
                InfoStatusOpenAttributeType.OPEN_EXCLUSIVE);
        session.close();

        return session.getID();
    }

    public void delete(UUID id) {
        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Ports"));

        InfoObject sessions = objectManager.get(identifications);

        sessions.deleteChild(new IdentificationDefinition(id));
    }
}
