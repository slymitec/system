package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.core.prototypes.IByteValueSupporter;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadComponentFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteComponentConsumer;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.*;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.Set;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessContextObject extends AChildCacheableObject<ProcessChildCacheEntity, ProcessObject> implements IByteValueSupporter<ProcessContextDefinition> {
    protected ProcessFactory factory;
    protected ProcessProcessorMediator processorMediator;

    private ProcessEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcess().getProcessId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getProcess().getProcessId());
    }

    private ProcessContextDefinition init(ProcessEntity process) {
        Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessContexts();

        byte[] source = null;

        for (ProcessProcessorReadComponentFunction resolver : resolvers) {
            source = resolver.apply(source, process);
        }

        return IByteValueSupporter.super.init(source);
    }

    private void flush(ProcessEntity process, ProcessContextDefinition value) {
        byte[] source = IByteValueSupporter.super.flush(value);

        Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessContexts();

        for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
            resolver.accept(process, source);
        }
    }

    public long getType() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessContextDefinition processContext = this.init(process);

            return processContext.getType();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void setType(long type) {
        if (this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();

        if (!currentProcess.getId().equals(this.base.getParentId())) {
            throw new ConditionRefuseException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessContextDefinition processContext = this.init(process);

            processContext.setType(type);

            this.flush(processContext);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public PathDefinition getPath() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessContextDefinition processContext = this.init(process);

            return processContext.getPath();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void setPath(PathDefinition path) {
        if (ObjectUtil.isAnyNull(path)) {
            throw new ConditionParametersException();
        }

        if (this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();

        if (!currentProcess.getId().equals(this.base.getParentId())) {
            throw new ConditionRefuseException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessContextDefinition processContext = this.init(process);

            processContext.setPath(path);

            this.flush(processContext);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public ApplicationDefinition getApplication() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessContextDefinition processContext = this.init(process);

            return processContext.getApplication();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void setApplication(ApplicationDefinition application) {
        if (ObjectUtil.isAnyNull(application)) {
            throw new ConditionParametersException();
        }

        if (this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();

        if (!currentProcess.getId().equals(this.base.getParentId())) {
            throw new ConditionRefuseException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessContextDefinition processContext = this.init(process);

            processContext.setApplication(application);

            this.flush(processContext);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public Map<String, String> getEnvironmentVariables() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessContextDefinition processContext = this.init(process);

            return CollectionUtil.unmodifiable(processContext.getEnvironmentVariables());
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariable) {
        if (ObjectUtil.isAnyNull(environmentVariable)) {
            throw new ConditionParametersException();
        }

        if (this.base.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            }
        } else {
            if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject currentProcess = processManager.getCurrent();

            if (!currentProcess.getId().equals(this.base.getParentId())) {
                throw new ConditionRefuseException();
            }
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessContextDefinition processContext = this.init(process);

            Map<String, String> processContextEnvironmentVariable = processContext.getEnvironmentVariables();
            processContextEnvironmentVariable.clear();
            processContextEnvironmentVariable.putAll(environmentVariable);

            this.flush(processContext);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public String getParameters() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessContextDefinition processContext = this.init(process);

            return processContext.getParameters();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void setParameters(String parameters) {
        if (ObjectUtil.isAnyNull(parameters)) {
            throw new ConditionParametersException();
        }

        if (this.base.isCurrent() || LogicalUtil.allNotEqual(this.base.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();

        if (!currentProcess.getId().equals(this.base.getParentId())) {
            throw new ConditionRefuseException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessContextDefinition processContext = this.init(process);

            processContext.setParameters(parameters);

            this.flush(processContext);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public PathDefinition getWorkFolder() {
        if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessContextDefinition processContext = this.init(process);

            return processContext.getWorkFolder();
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void setWorkFolder(PathDefinition workFolder) {
        if (ObjectUtil.isAnyNull(workFolder)) {
            throw new ConditionParametersException();
        }

        if (this.base.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            }
        } else {
            if (LogicalUtil.allNotEqual(this.base.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject currentProcess = processManager.getCurrent();

            if (!currentProcess.getId().equals(this.base.getParentId())) {
                throw new ConditionRefuseException();
            }
        }

        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);

            ProcessContextDefinition processContext = this.init(process);

            processContext.setWorkFolder(workFolder);

            this.flush(processContext);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }
}
