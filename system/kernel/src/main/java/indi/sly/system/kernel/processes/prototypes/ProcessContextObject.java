package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ProcessContextDefinition;
import indi.sly.system.kernel.processes.values.AppContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessContextObject extends ABytesValueProcessPrototype<ProcessContextDefinition> {
    protected ProcessObject process;

    private ProcessObject getParentProcessAndCheckIsCurrent() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject currentProcess = processManager.getCurrentProcess();

        if (!currentProcess.getID().equals(process.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        return currentProcess;
    }

    public AppContextDefinition getAppContext() {
        this.init();

        return this.value.getAppContext();
    }

    public void setAppContext(AppContextDefinition appContext) {
        if (ObjectUtil.isAnyNull(appContext)) {
            throw new ConditionParametersException();
        }

        if (this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.getParentProcessAndCheckIsCurrent();

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setAppContext(appContext);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
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
        if (ObjectUtil.isAnyNull(environmentVariable)) {
            throw new ConditionParametersException();
        }

        if (!this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Map<String, String> processContextEnvironmentVariable = this.value.getEnvironmentVariable();
            processContextEnvironmentVariable.clear();
            processContextEnvironmentVariable.putAll(environmentVariable);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Map<String, String> getParameters() {
        this.init();

        return this.value.getParameters();
    }

    public void setParameters(Map<String, String> parameters) {
        if (ObjectUtil.isAnyNull(parameters)) {
            throw new ConditionParametersException();
        }

        if (this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.getParentProcessAndCheckIsCurrent();

        try {
            this.lock(LockType.WRITE);
            this.init();

            Map<String, String> processContextParameters = this.value.getParameters();
            processContextParameters.clear();
            processContextParameters.putAll(parameters);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public List<IdentificationDefinition> getWorkFolder() {
        this.init();

        return this.value.getWorkFolder();
    }

    public void setWorkFolder(List<IdentificationDefinition> workFolder) {
        if (ObjectUtil.isAnyNull(workFolder)) {
            throw new ConditionParametersException();
        }

        if (this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.getParentProcessAndCheckIsCurrent();

        try {
            this.lock(LockType.WRITE);
            this.init();

            List<IdentificationDefinition> processContextWorkFolder = this.value.getWorkFolder();
            processContextWorkFolder.clear();
            processContextWorkFolder.addAll(workFolder);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
