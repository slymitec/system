package indi.sly.system.services.jobs.instances.prototypes.processors;

import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandledObjectDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ObjectManagerTaskInitializer extends ATaskInitializer {
    public ObjectManagerTaskInitializer() {
        this.register("get", this::get, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void get(TaskRunConsumer run, TaskContentObject content) {
        List<IdentificationDefinition> identifications = content.getParameterList(IdentificationDefinition.class, "identifications");

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoObject info = objectManager.get(identifications);

        UUID handle = info.cache(SpaceType.USER);

        HandledObjectDefinition handledObject = new HandledObjectDefinition();
        handledObject.setHandle(handle);
        handledObject.setType(info.getClass());

        content.setResult(handledObject);
    }
}
