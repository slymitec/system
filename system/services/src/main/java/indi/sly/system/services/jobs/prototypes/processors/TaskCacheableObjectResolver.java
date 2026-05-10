package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskProcessorContentFunction;
import indi.sly.system.services.jobs.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.jobs.values.HandleContextDefinition;
import indi.sly.system.services.jobs.values.TaskAttributeType;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskCacheableObjectResolver extends ATaskResolver {
    public TaskCacheableObjectResolver() {
        this.content = (task, status, threadContext) -> {
            if (LogicalUtil.isAnyExist(task.getAttribute(), TaskAttributeType.OBJECT_IS_CACHEABLE) && ObjectUtil.isAnyNull(threadContext.getCacheableObject())) {
                List<String> parameters = threadContext.getParameters();
                if (parameters.isEmpty()) {
                    throw new ConditionParametersException();
                }

                ATaskInitializer initializer = task.getInitializer();

                HandleContextDefinition handleContext = ObjectUtil.transferFromString(HandleContextDefinition.class, parameters.getFirst());
                ACacheableObject<?> cacheableObject = initializer.getCacheableObject(handleContext.getHandle());

                threadContext.setCacheableObject(cacheableObject);

                parameters = new ArrayList<>(parameters);
                parameters.removeFirst();

                threadContext.setParameters(parameters);
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
