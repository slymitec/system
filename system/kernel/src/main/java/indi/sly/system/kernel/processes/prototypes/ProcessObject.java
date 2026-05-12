package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessCacheEntity;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessObject extends ACacheableObject<ProcessCacheEntity> {
    protected ProcessFactory factory;
    protected ProcessProcessorMediator processorMediator;

    public UUID getId() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcessId())) {
            throw new ConditionContextException();
        }

        return this.cache.getProcessId();
    }

    public UUID getParentId() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcessId())) {
            throw new ConditionContextException();
        }

        return this.getSelf().getParentProcessID();
    }

    public boolean isCurrent() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcessId())) {
            throw new ConditionContextException();
        }

        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);

        ThreadObject thread = threadManager.getCurrent();

        return this.cache.getProcessId().equals(thread.getProcessId());
    }

    private ProcessEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getProcessId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getProcessId());
    }

    public ProcessStatusObject getStatus() {
        return this.factory.buildProcessStatus(this.processorMediator, this);
    }

    public ProcessCommunicationObject getCommunication() {
        return this.factory.buildProcessCommunication(this.processorMediator, this);
    }

    public ProcessContextObject getContext() {
        return this.factory.buildProcessContext(this.processorMediator, this);
    }

    public ProcessInfoTableObject getInfoTable() {
        return this.factory.buildProcessInfoTable(this.processorMediator, this);
    }

    public ProcessSessionObject getSession() {
        return this.factory.buildProcessSession(this.processorMediator, this);
    }

    public ProcessStatisticsObject getStatistics() {
        return this.factory.buildProcessStatistics(this.processorMediator, this);
    }

    public ProcessTokenObject getToken() {
        return this.factory.buildProcessToken(this.processorMediator, this);
    }
}
