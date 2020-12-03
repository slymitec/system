package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.AKernelException;
import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.definitions.ProcessContextDefinition;
import indi.sly.system.kernel.sessions.definitions.AppContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessContextObject extends ABytesProcessObject {
    @Override
    protected void read(byte[] source) {
        this.processContext = ObjectUtils.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtils.transferToByteArray(this.processContext);
    }

    private ProcessObject process;
    private ProcessContextDefinition processContext;

    public void setProcess(ProcessObject process) {
        this.process = process;
    }

    public AppContextDefinition getAppContext() {
        this.init();

        return processContext.getAppContext();
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

            this.processContext.setAppContext(appContext);

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

        return this.processContext.getEnvironmentVariable();
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

            Map<String, String> processContextEnvironmentVariable = this.processContext.getEnvironmentVariable();
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

        return processContext.getParameters();
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

            Map<String, String> processContextParameters = this.processContext.getParameters();
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

        return processContext.getSessionID();
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

            this.processContext.setSessionID(sessionID);

            this.fresh();
        } catch (AKernelException exception) {
            throw exception;
        } finally {
            this.lock(LockTypes.NONE);
        }
    }

    public List<Identification> getWorkFolder() {
        this.init();

        return processContext.getWorkFolder();
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

            List<Identification> processContextWorkFolder = this.processContext.getWorkFolder();
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
