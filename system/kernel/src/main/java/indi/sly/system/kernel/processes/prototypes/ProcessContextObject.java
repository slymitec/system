package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ApplicationDefinition;
import indi.sly.system.kernel.processes.values.ProcessContextDefinition;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessContextObject extends ABytesValueProcessObject<ProcessContextDefinition, ProcessObject> {
    public long getType() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        return this.value.getType();
    }

    public void setType(long type) {
        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        if (!process.getID().equals(this.parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setType(type);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public ApplicationDefinition getApplication() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        return this.value.getApplication();
    }

    public void setApplication(ApplicationDefinition application) {
        if (ObjectUtil.isAnyNull(application)) {
            throw new ConditionParametersException();
        }

        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        if (!process.getID().equals(this.parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setApplication(application);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Map<String, String> getEnvironmentVariables() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        return CollectionUtil.unmodifiable(this.value.getEnvironmentVariables());
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariable) {
        if (ObjectUtil.isAnyNull(environmentVariable)) {
            throw new ConditionParametersException();
        }

        if (this.parent.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            }
        } else {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            if (!process.getID().equals(this.parent.getParentID())) {
                throw new ConditionRefuseException();
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Map<String, String> processContextEnvironmentVariable = this.value.getEnvironmentVariables();
            processContextEnvironmentVariable.clear();
            processContextEnvironmentVariable.putAll(environmentVariable);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public String getParameters() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        return this.value.getParameters();
    }

    public void setParameters(String parameters) {
        if (ObjectUtil.isAnyNull(parameters)) {
            throw new ConditionParametersException();
        }

        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        if (!process.getID().equals(this.parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setParameters(parameters);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public List<IdentificationDefinition> getWorkFolder() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        return CollectionUtil.unmodifiable(this.value.getWorkFolder());
    }

    public void setWorkFolder(List<IdentificationDefinition> workFolder) {
        if (ObjectUtil.isAnyNull(workFolder)) {
            throw new ConditionParametersException();
        }

        if (this.parent.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            }
        } else {
            if (LogicalUtil.isAnyEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            if (!process.getID().equals(this.parent.getParentID())) {
                throw new ConditionRefuseException();
            }
        }

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
