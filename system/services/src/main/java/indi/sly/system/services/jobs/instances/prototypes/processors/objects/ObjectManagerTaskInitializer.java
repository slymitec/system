package indi.sly.system.services.jobs.instances.prototypes.processors.objects;

import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandleContextDefinition;
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
        List<String> parameters = content.getParameters();

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);

        InfoObject info = objectManager.get(ObjectUtil.transferFromString(PathDefinition.class, parameters.get(0)));

        UUID handle = info.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(info.getClass()), handle);

        content.setResult(handleContext);
    }
}
