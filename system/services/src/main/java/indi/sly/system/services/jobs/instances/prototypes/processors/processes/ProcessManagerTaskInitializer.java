package indi.sly.system.services.jobs.instances.prototypes.processors.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.values.ProcessAdditionalCreatorDefinition;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.prototypes.UserFactory;
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
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessManagerTaskInitializer extends ATaskInitializer {
    public ProcessManagerTaskInitializer() {
        this.cacheableObjectFunction = (_) -> this.coreManager.getManager(ProcessManager.class);

        this.register("getCurrent", this::getCurrent, TransactionType.INDEPENDENCE);
        this.register("getWithAuthorization", this::getWithAuthorization, TransactionType.INDEPENDENCE);
        this.register("get", this::get, TransactionType.INDEPENDENCE);
        this.register("create", this::create, TransactionType.INDEPENDENCE);
        this.register("endCurrent", this::endCurrent, TransactionType.INDEPENDENCE);
        this.register("end", this::end, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getCurrent(TaskRunConsumer run, TaskContentObject content) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();

        UUID handle = process.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(process.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getWithAuthorization(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        UserManager userManager = this.coreManager.getManager(UserManager.class);
        UserFactory userFactory = userManager.getFactory();

        if (parameters.size() < 2) {
            throw new ConditionParametersException();
        }

        UUID processId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());
        UUID accountAuthorizationHandle = ObjectUtil.transferFromString(UUID.class, parameters.get(1));
        AccountAuthorizationObject accountAuthorization = userFactory.rebuildAccountAuthorization(accountAuthorizationHandle);

        ProcessObject process = processManager.getWithAuthorization(processId, accountAuthorization);

        UUID handle = process.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(process.getClass()), handle);

        content.setResult(handleContext);
    }

    private void get(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID processId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        ProcessObject process = processManager.get(processId);

        UUID handle = process.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(process.getClass()), handle);

        content.setResult(handleContext);
    }

    private void create(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        UserManager userManager = this.coreManager.getManager(UserManager.class);
        UserFactory userFactory = userManager.getFactory();

        if (parameters.size() < 5) {
            throw new ConditionParametersException();
        }

        UUID accountAuthorizationHandle = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());
        AccountAuthorizationObject accountAuthorization = userFactory.rebuildAccountAuthorization(accountAuthorizationHandle);
        UUID fileIndex = ObjectUtil.transferFromString(UUID.class, parameters.get(1));
        String processParameters = ObjectUtil.transferFromString(String.class, parameters.get(2));
        PathRecord workFolder = ObjectUtil.transferFromString(PathRecord.class, parameters.get(3));
        ProcessAdditionalCreatorDefinition additionalCreator = ObjectUtil.transferFromString(ProcessAdditionalCreatorDefinition.class, parameters.get(4));

        ProcessObject process = processManager.create(accountAuthorization, fileIndex, processParameters, workFolder, additionalCreator);

        UUID handle = process.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(process.getClass()), handle);

        content.setResult(handleContext);
    }

    private void endCurrent(TaskRunConsumer run, TaskContentObject content) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        processManager.endCurrent();
    }

    private void end(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        UserManager userManager = this.coreManager.getManager(UserManager.class);

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID processId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        processManager.end(processId);
    }
}
