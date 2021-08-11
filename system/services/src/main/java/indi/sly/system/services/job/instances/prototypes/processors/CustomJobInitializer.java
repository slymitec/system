package indi.sly.system.services.job.instances.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.services.job.lang.JobRunConsumer;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.prototypes.processors.AJobInitializer;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobTransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomJobInitializer extends AJobInitializer {
    public CustomJobInitializer() {
        this.register("consumer", this::consumer, JobTransactionType.INDEPENDENCE);
    }

    @Override
    public void start(JobDefinition job) {
    }

    @Override
    public void finish(JobDefinition job) {
    }

    private void consumer(JobRunConsumer run, JobContentObject content) {
        Consumer resolver = content.getParameterOrDefault(Consumer.class, "method", null);

        if (ObjectUtil.isAnyNull(resolver)) {
            throw new ConditionParametersException();
        }

        resolver.accept();
    }
}
