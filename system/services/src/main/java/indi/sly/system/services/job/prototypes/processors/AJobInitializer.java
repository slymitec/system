package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.job.lang.JobInitializerRunMethodConsumer;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobInitializerRunDefinition;
import indi.sly.system.services.job.values.JobInitializerRunSummaryDefinition;
import indi.sly.system.services.job.values.JobTransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AJobInitializer extends APrototype {
    public AJobInitializer() {
        this.runs = new ConcurrentHashMap<>();
    }

    private final Map<String, JobInitializerRunDefinition> runs;

    protected final void register(String name, JobInitializerRunMethodConsumer runMethod) {
        this.register(name, runMethod, JobTransactionType.INDEPENDENCE);
    }

    protected final void register(String name, JobInitializerRunMethodConsumer runMethod, long runTransaction) {
        if (StringUtil.isNameIllegal(name) || ObjectUtil.isAnyNull(runMethod)) {
            throw new ConditionParametersException();
        }

        JobInitializerRunDefinition run = new JobInitializerRunDefinition();
        run.setMethod(runMethod);
        run.setTransaction(runTransaction);

        this.runs.put(name, run);
    }

    public abstract void start(JobDefinition job);

    public abstract void finish(JobDefinition job);

    public final boolean containRun(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        return this.runs.containsKey(name);
    }

    public final JobInitializerRunSummaryDefinition getRun(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        JobInitializerRunDefinition run = this.runs.getOrDefault(name, null);

        if (ObjectUtil.isAnyNull(run)) {
            throw new StatusNotExistedException();
        }

        JobInitializerRunSummaryDefinition runSummary = new JobInitializerRunSummaryDefinition();
        runSummary.setMethod(run.getMethod());
        runSummary.setTransaction(run.getTransaction());

        return runSummary;
    }
}
