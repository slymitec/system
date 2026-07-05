package indi.sly.system.services.jobs.instances.prototypes.processors.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessContextObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessContextObjectTaskInitializer extends ATaskInitializer {
    public ProcessContextObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ProcessManager.class).getFactory().rebuildProcessContext(handle);

        this.register("getType", this::getType, TransactionType.INDEPENDENCE);
        this.register("getPath", this::getPath, TransactionType.INDEPENDENCE);
        this.register("getApplication", this::getApplication, TransactionType.INDEPENDENCE);
        this.register("getEnvironmentVariables", this::getEnvironmentVariables, TransactionType.INDEPENDENCE);
        this.register("setEnvironmentVariables", this::setEnvironmentVariables, TransactionType.INDEPENDENCE);
        this.register("getParameters", this::getParameters, TransactionType.INDEPENDENCE);
        this.register("setParameters", this::setParameters, TransactionType.INDEPENDENCE);
        this.register("getWorkFolder", this::getWorkFolder, TransactionType.INDEPENDENCE);
        this.register("setWorkFolder", this::setWorkFolder, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getType(TaskRunConsumer run, TaskContentObject content) {
        ProcessContextObject processContext = content.getCacheableObject();

        content.setResult(processContext.getType());
    }

    private void getPath(TaskRunConsumer run, TaskContentObject content) {
        ProcessContextObject processContext = content.getCacheableObject();

        content.setResult(processContext.getPath());
    }

    private void getApplication(TaskRunConsumer run, TaskContentObject content) {
        ProcessContextObject processContext = content.getCacheableObject();

        content.setResult(processContext.getApplication());
    }

    private void getEnvironmentVariables(TaskRunConsumer run, TaskContentObject content) {
        ProcessContextObject processContext = content.getCacheableObject();

        content.setResult(processContext.getEnvironmentVariables());
    }

    private void setEnvironmentVariables(TaskRunConsumer run, TaskContentObject content) {
        ProcessContextObject processContext = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Map<String, String> environmentVariable = ObjectUtil.transferMapFromString(String.class, String.class, parameters.getFirst());

        processContext.setEnvironmentVariables(environmentVariable);
    }

    private void getParameters(TaskRunConsumer run, TaskContentObject content) {
        ProcessContextObject processContext = content.getCacheableObject();

        content.setResult(processContext.getParameters());
    }

    private void setParameters(TaskRunConsumer run, TaskContentObject content) {
        ProcessContextObject processContext = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        String processParameters = ObjectUtil.transferFromString(String.class, parameters.getFirst());

        processContext.setParameters(processParameters);
    }

    private void getWorkFolder(TaskRunConsumer run, TaskContentObject content) {
        ProcessContextObject processContext = content.getCacheableObject();

        content.setResult(processContext.getWorkFolder());
    }

    private void setWorkFolder(TaskRunConsumer run, TaskContentObject content) {
        ProcessContextObject processContext = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        PathRecord workFolder = ObjectUtil.transferFromString(PathRecord.class, parameters.getFirst());

        processContext.setWorkFolder(workFolder);
    }
}