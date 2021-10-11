package indi.sly.system.services.job.instances.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.processors.AInitializer;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.job.lang.TaskInitializerRunMethodConsumer;
import indi.sly.system.services.job.values.TaskDefinition;
import indi.sly.system.services.job.values.TaskInitializerRunDefinition;
import indi.sly.system.services.job.values.TaskInitializerRunSummaryDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ATaskInitializer extends AInitializer {
    public ATaskInitializer() {
        this.runs = new ConcurrentHashMap<>();
    }

    private final Map<String, TaskInitializerRunDefinition> runs;

    protected final void register(String name, TaskInitializerRunMethodConsumer runMethod) {
        this.register(name, runMethod, TransactionType.INDEPENDENCE);
    }

    protected final void register(String name, TaskInitializerRunMethodConsumer runMethod, long runTransaction) {
        if (StringUtil.isNameIllegal(name) || ObjectUtil.isAnyNull(runMethod)) {
            throw new ConditionParametersException();
        }

        TaskInitializerRunDefinition run = new TaskInitializerRunDefinition();
        run.setMethod(runMethod);
        run.setTransaction(runTransaction);

        this.runs.put(name, run);
    }

    public abstract void start(TaskDefinition task);

    public abstract void finish(TaskDefinition task);

    public final boolean containRun(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        return this.runs.containsKey(name);
    }

    public final TaskInitializerRunSummaryDefinition getRun(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        TaskInitializerRunDefinition run = this.runs.getOrDefault(name, null);

        if (ObjectUtil.isAnyNull(run)) {
            throw new StatusNotExistedException();
        }

        TaskInitializerRunSummaryDefinition runSummary = new TaskInitializerRunSummaryDefinition();
        runSummary.setTaskInitializerRun(run);

        return runSummary;
    }
}
