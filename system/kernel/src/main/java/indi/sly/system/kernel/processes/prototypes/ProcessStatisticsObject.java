package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.core.values.APersistentEntity;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadComponentFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteComponentConsumer;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessChildCacheEntity;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.values.ProcessStatisticsEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessStatisticsObject extends AChildCacheableObject<ProcessChildCacheEntity, ProcessObject> {
    protected ProcessFactory factory;
    protected ProcessProcessorMediator processorMediator;

    private ProcessEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcess().getProcessId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getProcess().getProcessId());
    }

    private ProcessStatisticsEntity init(ProcessEntity process) {
        Set<ProcessProcessorReadComponentFunction> resolvers = this.processorMediator.getReadProcessStatistics();

        APersistentEntity source = null;

        for (ProcessProcessorReadComponentFunction resolver : resolvers) {
            source = resolver.apply(source, process);
        }

        return (ProcessStatisticsEntity) source;
    }

    private void flush(ProcessEntity process, ProcessStatisticsEntity value) {
        Set<ProcessProcessorWriteComponentConsumer> resolvers = this.processorMediator.getWriteProcessStatistics();

        for (ProcessProcessorWriteComponentConsumer resolver : resolvers) {
            resolver.accept(process, value);
        }
    }

    public Map<Long, Long> getDate() {
        ProcessEntity process = this.getSelf();

        try {
            this.factory.lockProcess(this.cache.getProcess(), LockType.READ);

            ProcessStatisticsEntity processStatistics = this.init(process);

            return CollectionUtil.unmodifiable(processStatistics.getDate());
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void setDate(long dataTime, long value) {
        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {

            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.getDate().put(dataTime, value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public Map<String, Long> getStatistics() {
        Map<String, Long> statistics = new HashMap<>();

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.READ);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            statistics.put("StatusCumulation", processStatistics.getStatusCumulation());
            statistics.put("ThreadCumulation", processStatistics.getThreadCumulation());
            statistics.put("InfoCreate", processStatistics.getInfoCreate());
            statistics.put("InfoGet", processStatistics.getInfoGet());
            statistics.put("InfoQuery", processStatistics.getInfoQuery());
            statistics.put("InfoDelete", processStatistics.getInfoDelete());
            statistics.put("InfoDump", processStatistics.getInfoDump());
            statistics.put("InfoOpen", processStatistics.getInfoOpen());
            statistics.put("InfoClose", processStatistics.getInfoClose());
            statistics.put("InfoRead", processStatistics.getInfoRead());
            statistics.put("InfoWrite", processStatistics.getInfoWrite());
            statistics.put("SharedReadCount", processStatistics.getSharedReadCount());
            statistics.put("SharedReadBytes", processStatistics.getSharedReadBytes());
            statistics.put("SharedWriteCount", processStatistics.getSharedWriteCount());
            statistics.put("SharedWriteBytes", processStatistics.getSharedWriteBytes());
            statistics.put("PortCount", processStatistics.getPortCount());
            statistics.put("PortReadCount", processStatistics.getPortReadCount());
            statistics.put("PortReadBytes", processStatistics.getPortReadBytes());
            statistics.put("PortWriteCount", processStatistics.getPortWriteCount());
            statistics.put("PortWriteBytes", processStatistics.getPortWriteBytes());
            statistics.put("SignalReadCount", processStatistics.getSignalReadCount());
            statistics.put("SignalWriteCount", processStatistics.getSignalWriteCount());
            statistics.put("IoCreate", processStatistics.getIoCreate());
            statistics.put("IoStatus", processStatistics.getIoStatus());
            statistics.put("IoReadCount", processStatistics.getIoReadCount());
            statistics.put("IoReadBytes", processStatistics.getIoReadBytes());
            statistics.put("IoWriteCount", processStatistics.getIoWriteCount());
            statistics.put("IoWriteBytes", processStatistics.getIoWriteBytes());

            return CollectionUtil.unmodifiable(statistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.READ);
        }
    }

    public void addStatusCumulation(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetStatusCumulation(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addThreadCumulation(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetThreadCumulation(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addInfoCreate(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetInfoCreate(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addInfoGet(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetInfoGet(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addInfoQuery(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetInfoQuery(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addInfoDelete(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {

            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetInfoDelete(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addInfoDump(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetInfoDump(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addInfoOpen(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetInfoOpen(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addInfoClose(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetInfoClose(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addInfoRead(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetInfoRead(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addInfoWrite(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetInfoWrite(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addSharedReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetSharedReadCount(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addSharedReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetSharedReadBytes(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addSharedWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetSharedWriteCount(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addSharedWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetSharedWriteBytes(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addPortCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetPortCount(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addPortReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetPortReadCount(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addPortReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetPortReadBytes(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addPortWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetPortWriteCount(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addPortWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetPortWriteBytes(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addSignalReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {

            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetSignalReadCount(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addSignalWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetSignalWriteCount(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addIoCreate(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetIoCreate(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addIoStatus(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetIoStatus(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addIoReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetIoReadCount(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addIoReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetIoReadBytes(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addIoWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetIoWriteCount(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }

    public void addIoWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        if (!this.base.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        ProcessEntity process = this.getSelf();

        this.factory.lockProcess(this.cache.getProcess(), LockType.WRITE);
        try {
            ProcessStatisticsEntity processStatistics = this.init(process);

            processStatistics.offsetIoWriteBytes(value);

            this.flush(process, processStatistics);
        } finally {
            this.factory.unlockProcess(this.cache.getProcess(), LockType.WRITE);
        }
    }
}
