package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.environment.values.CacheDurationType;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskProcessorContentFunction;
import indi.sly.system.services.jobs.prototypes.mediators.TaskProcessorMediator;
import indi.sly.system.services.jobs.values.TaskAttributeType;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskCacheableObjectResolver extends AResolver implements ITaskResolver {
    public TaskCacheableObjectResolver() {
        this.content = (task, status, threadContext) -> {
            if (!LogicalUtil.isAnyExist(task.getAttribute(), TaskAttributeType.OBJECT_IS_NOT_CACHEABLE) && ObjectUtil.isAnyNull(threadContext.getCacheableObject())) {
                ATaskInitializer initializer = task.getInitializer();

                ACacheableObject<?> cacheableObject = initializer.getCacheableObject(status.getHandle());
                cacheableObject.expire(CacheDurationType.RUNNING);

                threadContext.setCacheableObject(cacheableObject);
            }

            return threadContext;
        };
    }

    @Override
    public int order() {
        return 1;
    }

    private final TaskProcessorContentFunction content;

    @Override
    public void resolve(TaskDefinition task, TaskProcessorMediator processorMediator) {
        processorMediator.getContents().add(this.content);
    }
}
