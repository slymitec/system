package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ProcessContextDefinition;
import indi.sly.system.kernel.processes.values.ApplicationDefinition;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.processes.values.ThreadContextType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessContextObject extends ABytesValueProcessPrototype<ProcessContextDefinition> {
    protected ProcessObject process;

    private ProcessObject getParentProcessAndCheckIsCurrent() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject currentProcess = processManager.getCurrent();

        if (!currentProcess.getID().equals(process.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        return currentProcess;
    }

    private ProcessTokenObject getParentProcessTokenAndCheckIsCurrent() {
        return this.getParentProcessAndCheckIsCurrent().getToken();
    }

    public long getType() {
        this.init();

        return this.value.getType();
    }

    public void setType(long type) {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.INTERRUPTED)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (this.value.getType() != ThreadContextType.NULL) {
                throw new StatusAlreadyExistedException();
            }
            this.value.setType(type);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public ApplicationDefinition getApplication() {
        this.init();

        return this.value.getApplication();
    }

    public void setApplication(ApplicationDefinition application) {
        if (ObjectUtil.isAnyNull(application)) {
            throw new ConditionParametersException();
        }

        if (this.process.isCurrent()) {
            throw new ConditionPermissionsException();
        }

        this.getParentProcessAndCheckIsCurrent();

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setApplication(application);

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
