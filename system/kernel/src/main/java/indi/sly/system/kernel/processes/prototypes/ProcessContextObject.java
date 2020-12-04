package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.AKernelException;
import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessPrototype;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ProcessContextDefinition;
import indi.sly.system.kernel.sessions.values.AppContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessContextObject extends ABytesProcessPrototype<ProcessContextDefinition> {
    private ProcessObject process;

    public void setProcess(ProcessObject process) {
        this.process = process;
    }

    public AppContextDefinition getAppContext() {
        this.init();

        return this.value.getAppContext();
    }

    public void setAppContext(AppContextDefinition appContext) {
        if (ObjectUtils.isAnyNull(appContext)) {
            throw new ConditionParametersException();
        }

        if (this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject parentProcess = processManager.getCurrentProcess();

        if (!parentProcess.getID().equals(this.process.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            this.value.setAppContext(appContext);

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }
    }

    public Map<String, String> getEnvironmentVariable() {
        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.init();

        return this.value.getEnvironmentVariable();
    }

    public void setEnvironmentVariable(Map<String, String> environmentVariable) {
        if (ObjectUtils.isAnyNull(environmentVariable)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            Map<String, String> processContextEnvironmentVariable = this.value.getEnvironmentVariable();
            processContextEnvironmentVariable.clear();
            processContextEnvironmentVariable.putAll(environmentVariable);

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }
    }

    public Map<String, String> getParameters() {
        this.init();

        return this.value.getParameters();
    }

    public void setParameters(Map<String, String> parameters) {
        if (ObjectUtils.isAnyNull(parameters)) {
            throw new ConditionParametersException();
        }

        if (this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject parentProcess = processManager.getCurrentProcess();

        if (!parentProcess.getID().equals(this.process.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            Map<String, String> processContextParameters = this.value.getParameters();
            processContextParameters.clear();
            processContextParameters.putAll(parameters);

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }
    }

    public UUID getSessionID() {
        this.init();

        return this.value.getSessionID();
    }

    public void setSessionID(UUID sessionID) {
        if (UUIDUtils.isAnyNullOrEmpty(sessionID)) {
            throw new ConditionParametersException();
        }

        if (this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject parentProcess = processManager.getCurrentProcess();

        if (!parentProcess.getID().equals(this.process.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            this.value.setSessionID(sessionID);

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }
    }

    public List<Identification> getWorkFolder() {
        this.init();

        return this.value.getWorkFolder();
    }

    public void setWorkFolder(List<Identification> workFolder) {
        if (ObjectUtils.isAnyNull(workFolder)) {
            throw new ConditionParametersException();
        }

        if (this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject parentProcess = processManager.getCurrentProcess();

        if (!parentProcess.getID().equals(this.process.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        try {
            this.lock(LockTypes.WRITE);
            this.init();

            List<Identification> processContextWorkFolder = this.value.getWorkFolder();
            processContextWorkFolder.clear();
            processContextWorkFolder.addAll(workFolder);

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }
    }
}
