package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.core.prototypes.AChildDefinitionObject;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.services.jobs.lang.TaskProcessorFinishConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorRunConsumer;
import indi.sly.system.services.jobs.prototypes.mediators.TaskProcessorMediator;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskContentObject extends AChildDefinitionObject<TaskDefinition, TaskObject> {
    protected TaskProcessorMediator processorMediator;

    protected TaskStatusDefinition status;
    protected ThreadContextObject threadContext;

    @SuppressWarnings("unchecked")
    public <T extends ACacheableObject<?>> T getCacheableObject() {
        return (T) this.threadContext.getCacheableObject();
    }

    public List<String> getParameters() {
        return this.threadContext.getParameters();
    }

    public void setParameter(List<String> parameters) {
        if (ValueUtil.isAnyNullOrEmpty(parameters)) {
            throw new ConditionParametersException();
        }

        this.threadContext.setParameters(parameters);
    }

    public Object getResult() {
        return this.threadContext.getResult();
    }

    public void setResult(Object value) {
        this.threadContext.setResult(value);
    }

    public ASystemException getException() {
        return this.threadContext.getRunException();
    }

    public void setException(ASystemException exception) {
        if (ObjectUtil.isAnyNull(exception)) {
            throw new ConditionParametersException();
        }

        this.threadContext.setRunException(exception);
    }

    public synchronized void run(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        List<TaskProcessorRunConsumer> runResolvers = this.processorMediator.getRuns();

        for (TaskProcessorRunConsumer resolver : runResolvers) {
            resolver.accept(this.definition, this.status, name, this);
        }

        List<TaskProcessorFinishConsumer> finishResolvers = this.processorMediator.getFinishes();

        for (TaskProcessorFinishConsumer resolver : finishResolvers) {
            resolver.accept(this.definition, this.status, name, this);
        }
    }
}
