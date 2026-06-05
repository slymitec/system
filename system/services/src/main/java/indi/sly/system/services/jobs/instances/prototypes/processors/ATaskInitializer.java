package indi.sly.system.services.jobs.instances.prototypes.processors;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.core.prototypes.processors.AInitializer;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.lang.TaskInitializerRunMethodConsumer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskInitializerRunDefinition;
import indi.sly.system.services.jobs.values.TaskInitializerRunSummaryDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ATaskInitializer extends AInitializer {
    public ATaskInitializer() {
        this.runs = new ConcurrentHashMap<>();

        this.register("cache", this::cache, TransactionType.WHATEVER);
        this.register("unCache", this::unCache, TransactionType.WHATEVER);
        this.register("expire", this::expire, TransactionType.WHATEVER);
    }

    private final Map<String, TaskInitializerRunDefinition> runs;
    protected Function1<? extends ACacheableObject<?>, UUID> cacheableObjectFunction;

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

    public final ACacheableObject<?> getCacheableObject(UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(this.cacheableObjectFunction)) {
            throw new ConditionContextException();
        }

        return this.cacheableObjectFunction.apply(handle);
    }

    private void cache(TaskRunConsumer run, TaskContentObject content) {
        ACacheableObject<?> cacheableObject = content.getCacheableObject();

        content.setResult(cacheableObject.getHandle());
    }

    private void unCache(TaskRunConsumer run, TaskContentObject content) {
        ACacheableObject<?> cacheableObject = content.getCacheableObject();

        if (ObjectUtil.isAnyNull(cacheableObject)) {
            throw new StatusNotSupportedException();
        } else {
            cacheableObject.uncache();
        }
    }

    private void expire(TaskRunConsumer run, TaskContentObject content) {
        ACacheableObject<?> cacheableObject = content.getCacheableObject();
        if (ObjectUtil.isAnyNull(cacheableObject)) {
            throw new StatusNotSupportedException();
        }

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        long duration = ObjectUtil.transferFromString(Long.class, parameters.getFirst());

        cacheableObject.expire(duration);
    }
}
