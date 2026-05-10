package indi.sly.system.services.jobs.instances.prototypes.processors.core;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.CoreManager;
import indi.sly.system.kernel.core.systemversion.prototypes.SystemVersionObject;
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

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SystemVersionObjectTaskInitializer extends ATaskInitializer {
    public SystemVersionObjectTaskInitializer() {
        this.register("getSystemVersion", this::getSystemVersion, TransactionType.WHATEVER);
    }

    @Override
    public void start(TaskDefinition task) {

    }

    @Override
    public void finish(TaskDefinition task) {

    }

    private SystemVersionObject getCacheableObject(TaskContentObject content) {
        List<String> parameters = content.getParameters();

        HandleContextDefinition handleContext = ObjectUtil.transferFromString(HandleContextDefinition.class, parameters.get(0));

        CoreManager coreManager = this.coreManager.getManager(CoreManager.class);
        return coreManager.getFactory().rebuildSystemVersion(handleContext.getHandle());
    }

    private String getSystemVersion(TaskRunConsumer run, TaskContentObject content) {
        SystemVersionObject systemVersion = this.getCacheableObject(content);

        return systemVersion.getSystemVersion();
    }
}
