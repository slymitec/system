package indi.sly.system.services.jobs.instances.prototypes.processors.services.instances;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.objects.ObjectManager;
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

    private void getDependencies(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getDependencies());
    }

    private void getSecret(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getSecret());
    }

    private void setSecret(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        String secret = ObjectUtil.transferFromString(String.class, parameters.getFirst());

        serviceContent.setSecret(secret);
    }

    private void getPath(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getPath());
    }

    private void setPath(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        PathRecord path = ObjectUtil.transferFromString(PathRecord.class, parameters.getFirst());

        serviceContent.setPath(path);
    }

    private void getAccountId(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getAccountId());
    }

    private void setAccountId(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID accountId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        serviceContent.setAccountId(accountId);
    }

    private void getMode(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getMode());
    }

    private void setMode(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        long mode = ObjectUtil.transferFromString(Long.class, parameters.getFirst());

        serviceContent.setMode(mode);
    }

    private void getStart(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getStart());
    }

    private void setStart(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        long start = ObjectUtil.transferFromString(Long.class, parameters.getFirst());

        serviceContent.setStart(start);
    }

    private void getEnvironmentVariables(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getEnvironmentVariables());
    }

    private void setEnvironmentVariables(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Map<String, String> environmentVariable = ObjectUtil.transferMapFromString(String.class, String.class, parameters.getFirst());

        serviceContent.setEnvironmentVariables(environmentVariable);
    }

    private void getParameters(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        content.setResult(serviceContent.getParameters());
    }

    private void setParameters(TaskContentObject content) {
        ServiceContentObject serviceContent = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        String processParameters = ObjectUtil.transferFromString(String.class, parameters.getFirst());

        serviceContent.setParameters(processParameters);
    }
}
