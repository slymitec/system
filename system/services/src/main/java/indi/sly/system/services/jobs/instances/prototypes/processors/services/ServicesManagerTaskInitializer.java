package indi.sly.system.services.jobs.instances.prototypes.processors.services;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.services.ServiceManager;
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
public class ServicesManagerTaskInitializer extends ATaskInitializer {
    public ServicesManagerTaskInitializer() {
        this.register("createService", this::createService, TransactionType.INDEPENDENCE);
        this.register("deleteService", this::deleteService, TransactionType.INDEPENDENCE);
        this.register("start", this::start, TransactionType.INDEPENDENCE);
        this.register("stop", this::stop, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void createService(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        ServiceManager serviceManager = this.coreManager.getManager(ServiceManager.class);

        if (parameters.size() < 9) {
            throw new ConditionParametersException();
        }

        UUID serviceId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());
        List<UUID> dependencies = ObjectUtil.transferListFromString(UUID.class, parameters.get(2));
        String secret = ObjectUtil.transferFromString(String.class, parameters.get(3));
        PathDefinition path = ObjectUtil.transferFromString(PathDefinition.class, parameters.get(4));
        UUID accountId = ObjectUtil.transferFromString(UUID.class, parameters.get(5));
        long mode = ObjectUtil.transferFromString(Long.class, parameters.get(6));
        long start = ObjectUtil.transferFromString(Long.class, parameters.get(7));
        Map<String, String> environmentVariables = ObjectUtil.transferMapFromString(String.class, String.class, parameters.get(8));
        String serviceParameters = ObjectUtil.transferFromString(String.class, parameters.get(9));

        serviceManager.createService(serviceId, dependencies, secret, path, accountId, mode, start, environmentVariables, serviceParameters);
    }

    private void deleteService(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        ServiceManager serviceManager = this.coreManager.getManager(ServiceManager.class);

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID serviceId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        serviceManager.deleteService(serviceId);
    }

    private void start(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        ServiceManager serviceManager = this.coreManager.getManager(ServiceManager.class);

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID serviceId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        serviceManager.start(serviceId);
    }

    private void stop(TaskRunConsumer run, TaskContentObject content) {
        List<String> parameters = content.getParameters();

        ServiceManager serviceManager = this.coreManager.getManager(ServiceManager.class);

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID serviceId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        serviceManager.stop(serviceId);
    }
}
