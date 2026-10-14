package indi.sly.system.kernel.services.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.PathRecord;
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
public class ServiceContentObject extends AInfoContentObject {
    public List<UUID> getDependencies() {
        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        return CollectionUtil.unmodifiable(service.getDependencies());
    }

    public void setDependencies(List<UUID> dependencies) {
        if (ObjectUtil.isNull(dependencies)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        service.getDependencies().clear();
        service.getDependencies().addAll(dependencies);

        this.write(ObjectUtil.transferToJsonByteArray(service));
    }

    public String getSecret() {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        return service.getSecret();
    }

    public void setSecret(String secret) {
        if (ValueUtil.isAnyNullOrEmpty(secret)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        service.setSecret(secret);

        this.write(ObjectUtil.transferToJsonByteArray(service));
    }

    public PathRecord getPath() {
        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        return service.getPath();
    }

    public void setPath(PathRecord path) {
        if (ObjectUtil.isNull(path)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        service.setPath(path);

        this.write(ObjectUtil.transferToJsonByteArray(service));
    }

    public UUID getAccountId() {
        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        return service.getAccountId();
    }

    public void setAccountId(UUID accountId) {
        if (ValueUtil.isAnyNullOrEmpty(accountId)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        service.setAccountId(accountId);

        this.write(ObjectUtil.transferToJsonByteArray(service));
    }

    public long getMode() {
        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        return service.getMode();
    }

    public void setMode(long mode) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        service.setMode(mode);

        this.write(ObjectUtil.transferToJsonByteArray(service));
    }

    public long getStart() {
        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        return service.getStart();
    }

    public void setStart(long start) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        service.setStart(start);

        this.write(ObjectUtil.transferToJsonByteArray(service));
    }

    public Map<String, String> getEnvironmentVariables() {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        return CollectionUtil.unmodifiable(service.getEnvironmentVariables());
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        if (ObjectUtil.isNull(environmentVariables)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        service.getEnvironmentVariables().clear();
        service.getEnvironmentVariables().putAll(environmentVariables);

        this.write(ObjectUtil.transferToJsonByteArray(service));
    }

    public String getParameters() {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        if (!currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY)) {
            throw new ConditionRefuseException();
        }

        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

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

        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

        service.setParameters(parameters);

        this.write(ObjectUtil.transferToJsonByteArray(service));
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

        ServiceDefinition service = ObjectUtil.transferFromJsonByteArray(ServiceDefinition.class, this.read());

        if (ObjectUtil.isNull(service)) {
            throw new StatusUnreadableException();
        }

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

        this.write(ObjectUtil.transferToJsonByteArray(service));
    }
}
