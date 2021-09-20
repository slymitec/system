package indi.sly.system.kernel.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionManager extends APrototype {
    public SessionContentObject getAndOpen(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Sessions"),
                new IdentificationDefinition(id));

        InfoObject session = objectManager.get(identifications);
        session.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);

        return (SessionContentObject) session.getContent();
    }

    public void close(SessionContentObject sessionContent) {
        if (ObjectUtil.isAnyNull(sessionContent)) {
            throw new ConditionParametersException();
        }

        sessionContent.close();
    }

    public UUID create() {
        List<IdentificationDefinition> identifications = List.of(new IdentificationDefinition("Sessions"));

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        InfoObject sessions = objectManager.get(identifications);

        UUID typeID = this.factoryManager.getKernelSpace().getConfiguration().PROCESSES_SESSION_INSTANCE_ID;

        InfoObject session = sessions.createChildAndOpen(typeID, new IdentificationDefinition(UUIDUtil.createRandom()),
                InfoOpenAttributeType.OPEN_SHARED_WRITE);
        SessionContentObject sessionContent = (SessionContentObject) session.getContent();

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
