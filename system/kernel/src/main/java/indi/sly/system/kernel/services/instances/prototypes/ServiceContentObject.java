package indi.sly.system.kernel.services.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.prototypes.IByteValueSupporter;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import indi.sly.system.kernel.services.instances.values.ServiceDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceContentObject extends AInfoContentObject implements IByteValueSupporter<ServiceDefinition> {
    public List<UUID> getDependencies() {
        ServiceDefinition service = this.init(this.read());

        return CollectionUtil.unmodifiable(service.getDependencies());
    }

    public void setDependencies(List<UUID> dependencies) {
        if (ObjectUtil.isAnyNull(dependencies)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = this.init(this.read());

        service.getDependencies().clear();
        service.getDependencies().addAll(dependencies);

        this.write(this.flush(service));
    }

    public String getSecret() {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = this.init(this.read());

        return service.getSecret();
    }

    public void setSecret(String secret) {
        if (ValueUtil.isAnyNullOrEmpty()) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = this.init(this.read());

        service.setSecret(secret);

        this.write(this.flush(service));
    }

    public PathRecord getPath() {
        ServiceDefinition service = this.init(this.read());

        return service.getPath();
    }

    public void setPath(PathRecord path) {
        if (ObjectUtil.isAnyNull(path)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = this.init(this.read());

        service.setPath(path);

        this.write(this.flush(service));
    }

    public UUID getAccountId() {
        ServiceDefinition service = this.init(this.read());

        return service.getAccountId();
    }

    public void setAccountId(UUID accountId) {
        if (ValueUtil.isAnyNullOrEmpty()) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = this.init(this.read());

        service.setAccountId(accountId);

        this.write(this.flush(service));
    }

    public long getMode() {
        ServiceDefinition service = this.init(this.read());

        return service.getMode();
    }

    public void setMode(long mode) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = this.init(this.read());

        service.setMode(mode);

        this.write(this.flush(service));
    }

    public long getStart() {
        ServiceDefinition service = this.init(this.read());

        return service.getStart();
    }

    public void setStart(long start) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = this.init(this.read());

        service.setStart(start);

        this.write(this.flush(service));
    }

    public Map<String, String> getEnvironmentVariables() {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = this.init(this.read());

        return CollectionUtil.unmodifiable(service.getEnvironmentVariables());
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        if (ObjectUtil.isAnyNull(environmentVariables)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = this.init(this.read());

        service.getEnvironmentVariables().clear();
        service.getEnvironmentVariables().putAll(environmentVariables);

        this.write(this.flush(service));
    }

    public String getParameters() {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = this.init(this.read());

        return service.getParameters();
    }

    public void setParameters(String parameters) {
        if (ValueUtil.isAnyNullOrEmpty(parameters)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = this.init(this.read());

        service.setParameters(parameters);

        this.write(this.flush(service));
    }

    public void set(ServiceDefinition definition) {
        if (ValueUtil.isAnyNullOrEmpty(definition)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = this.init(this.read());

        service.getDependencies().clear();
        service.getDependencies().addAll(definition.getDependencies());
        service.setSecret(definition.getSecret());
        service.setPath(definition.getPath());
        service.setAccountId(definition.getAccountId());
        service.setMode(definition.getMode());
        service.setStart(definition.getStart());
        service.getEnvironmentVariables().clear();
        service.getEnvironmentVariables().putAll(definition.getEnvironmentVariables());
        service.setParameters(definition.getParameters());

        this.write(this.flush(service));
    }
}
