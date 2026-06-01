package indi.sly.system.services.jobs.instances.prototypes.processors.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
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
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessTokenObjectTaskInitializer extends ATaskInitializer {
    public ProcessTokenObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ProcessManager.class).getFactory().rebuildProcessToken(handle);

        this.register("getAccountId", this::getAccountId, TransactionType.INDEPENDENCE);
        this.register("getPrivileges", this::getPrivileges, TransactionType.INDEPENDENCE);
        this.register("setPrivileges", this::setPrivileges, TransactionType.INDEPENDENCE);
        this.register("getLimits", this::getLimits, TransactionType.INDEPENDENCE);
        this.register("setLimits", this::setLimits, TransactionType.INDEPENDENCE);
        this.register("getRoles", this::getRoles, TransactionType.INDEPENDENCE);
        this.register("initDefaultRoles", this::initDefaultRoles, TransactionType.INDEPENDENCE);
        this.register("addRoles", this::addRoles, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getAccountId(TaskRunConsumer run, TaskContentObject content) {
        ProcessTokenObject processToken = content.getCacheableObject();

        content.setResult(processToken.getAccountId());
    }

    private void getPrivileges(TaskRunConsumer run, TaskContentObject content) {
        ProcessTokenObject processToken = content.getCacheableObject();

        content.setResult(processToken.getPrivileges());
    }

    private void setPrivileges(TaskRunConsumer run, TaskContentObject content) {
        ProcessTokenObject processToken = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        long privileges = ObjectUtil.transferFromString(Long.class, parameters.getFirst());

        processToken.setPrivileges(privileges);
    }

    private void getLimits(TaskRunConsumer run, TaskContentObject content) {
        ProcessTokenObject processToken = content.getCacheableObject();

        content.setResult(processToken.getLimits());
    }

    private void setLimits(TaskRunConsumer run, TaskContentObject content) {
        ProcessTokenObject processToken = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Map<Long, Integer> limits = ObjectUtil.transferMapFromString(Long.class, Integer.class, parameters.getFirst());

        processToken.setLimits(limits);
    }

    private void getRoles(TaskRunConsumer run, TaskContentObject content) {
        ProcessTokenObject processToken = content.getCacheableObject();

        content.setResult(processToken.getRoles());
    }

    private void initDefaultRoles(TaskRunConsumer run, TaskContentObject content) {
        ProcessTokenObject processToken = content.getCacheableObject();

        processToken.initDefaultRoles();
    }

    private void addRoles(TaskRunConsumer run, TaskContentObject content) {
        ProcessTokenObject processToken = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Set<UUID> roles = ObjectUtil.transferSetFromString(UUID.class, parameters.getFirst());

        processToken.addRoles(roles);
    }
}