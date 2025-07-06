package indi.sly.system.services.jobs.instances.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandledObjectDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessTaskInitializer extends ATaskInitializer {
    public ProcessTaskInitializer() {
        this.register("getCurrent", this::getCurrent, TransactionType.INDEPENDENCE);
        this.register("get", this::get, TransactionType.INDEPENDENCE);
        this.register("create", this::create, TransactionType.INDEPENDENCE);
        this.register("endCurrent", this::endCurrent, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getCurrent(TaskRunConsumer run, TaskContentObject content) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();

        UUID handle = process.cache(SpaceType.USER);

        HandledObjectDefinition handledObject = new HandledObjectDefinition();
        handledObject.setHandle(handle);
        handledObject.setType(process.getClass());

        content.setResult(handledObject);
    }

    private void get(TaskRunConsumer run, TaskContentObject content) {
        UUID processID = content.getParameter(UUID.class, "processID");
        if (ValueUtil.isAnyNullOrEmpty(processID)) {
            throw new ConditionParametersException();
        }

        AccountAuthorizationObject accountAuthorization = content.getCacheByParameterNameOrDefault("accountAuthorizationID", null);

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process;
        if (ObjectUtil.isAnyNull(accountAuthorization)) {
            process = processManager.get(processID);
        } else {
            process = processManager.get(processID, accountAuthorization);
        }

        UUID handle = process.cache(SpaceType.USER);

        HandledObjectDefinition handledObject = new HandledObjectDefinition();
        handledObject.setHandle(handle);
        handledObject.setType(process.getClass());

        content.setResult(handledObject);
    }

    private void create(TaskRunConsumer run, TaskContentObject content) {
        AccountAuthorizationObject accountAuthorization = content.getCacheByParameterNameOrDefault("accountAuthorizationID", null);
        UUID fileIndex = content.getParameterOrNull(UUID.class, "fileIndex");
        String parameters = content.getParameterOrNull("parameters");
        List<IdentificationDefinition> workFolder = content.getParameterListOrNull(IdentificationDefinition.class, "workFolder");

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.create(accountAuthorization, fileIndex, parameters, workFolder);

        UUID handle = process.cache(SpaceType.USER);

        HandledObjectDefinition handledObject = new HandledObjectDefinition();
        handledObject.setHandle(handle);
        handledObject.setType(process.getClass());

        content.setResult(handledObject);
    }

    private void endCurrent(TaskRunConsumer run, TaskContentObject content) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        processManager.endCurrent();
    }
}
