package indi.sly.system.services.jobs.instances.prototypes.processors.services.instances;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.processes.prototypes.ProcessContextObject;
import indi.sly.system.kernel.security.instances.prototypes.AuditContentObject;
import indi.sly.system.kernel.services.instances.prototypes.ServiceContentObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceContentObjectTaskInitializer extends ATaskInitializer {
    public ServiceContentObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ObjectManager.class).getFactory().rebuildInfoContent(handle);

        this.register("getDependencies", this::getDependencies, TransactionType.INDEPENDENCE);
        this.register("getSecret", this::getSecret, TransactionType.INDEPENDENCE);
        this.register("setSecret", this::setSecret, TransactionType.INDEPENDENCE);
        this.register("getPath", this::getPath, TransactionType.INDEPENDENCE);
        this.register("setPath", this::setPath, TransactionType.INDEPENDENCE);
        this.register("getAccountId", this::getAccountId, TransactionType.INDEPENDENCE);
        this.register("setAccountId", this::setAccountId, TransactionType.INDEPENDENCE);
        this.register("getMode", this::getMode, TransactionType.INDEPENDENCE);
        this.register("setMode", this::setMode, TransactionType.INDEPENDENCE);
        this.register("getStart", this::getStart, TransactionType.INDEPENDENCE);
        this.register("setStart", this::setStart, TransactionType.INDEPENDENCE);
        this.register("getEnvironmentVariables", this::getEnvironmentVariables, TransactionType.INDEPENDENCE);
        this.register("setEnvironmentVariables", this::setEnvironmentVariables, TransactionType.INDEPENDENCE);
        this.register("getParameters", this::getParameters, TransactionType.INDEPENDENCE);
        this.register("setParameters", this::setParameters, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getDependencies(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getDependencies());
    }

    private void getSecret(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getSecret());
    }

    private void setSecret(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        String secret = ObjectUtil.transferFromString(String.class, parameters.getFirst());

        serviceContent.setSecret(secret);
    }

    private void getPath(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getPath());
    }

    private void setPath(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        PathDefinition path = ObjectUtil.transferFromString(PathDefinition.class, parameters.getFirst());

        serviceContent.setPath(path);
    }

    private void getAccountId(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getAccountId());
    }

    private void setAccountId(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID accountId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        serviceContent.setAccountId(accountId);
    }

    private void getMode(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getMode());
    }

    private void setMode(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        long mode = ObjectUtil.transferFromString(Long.class, parameters.getFirst());

        serviceContent.setMode(mode);
    }

    private void getStart(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getStart());
    }

    private void setStart(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        long start = ObjectUtil.transferFromString(Long.class, parameters.getFirst());

        serviceContent.setStart(start);
    }

    private void getEnvironmentVariables(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getEnvironmentVariables());
    }

    private void setEnvironmentVariables(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Map<String, String> environmentVariable = ObjectUtil.transferMapFromString(String.class, String.class, parameters.getFirst());

        serviceContent.setEnvironmentVariables(environmentVariable);
    }

    private void getParameters(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getParameters());
    }

    private void setParameters(TaskRunConsumer run, TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        String processParameters = ObjectUtil.transferFromString(String.class, parameters.getFirst());

        serviceContent.setParameters(processParameters);
    }
}
