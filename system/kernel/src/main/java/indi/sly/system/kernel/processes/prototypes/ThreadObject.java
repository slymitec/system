package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.core.prototypes.ADefinitionObject;
import indi.sly.system.kernel.processes.values.ThreadDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadObject extends ADefinitionObject<ThreadDefinition> {
    public ThreadObject() {
        this.definition = new ThreadDefinition();
    }

    public UUID getId() {
        return this.definition.getId();
    }

    public UUID getProcessId() {
        return this.definition.getProcessId();
    }

    public void setProcessId(UUID id) {
        this.definition.setProcessId(id);
    }

    public synchronized ThreadStatusObject getStatus() {
        ThreadStatusObject threadStatus = this.coreManager.create(ThreadStatusObject.class);

        threadStatus.setBase(this);
        threadStatus.setDefinition(this.definition);

        return threadStatus;
    }

    public synchronized ThreadContextObject getContext() {
        ThreadContextObject threadContext = this.coreManager.create(ThreadContextObject.class);

        threadContext.setBase(this);
        threadContext.setDefinition(this.definition.getContext());

        return threadContext;
    }

    public synchronized ThreadStatisticsObject getStatistics() {
        ThreadStatisticsObject threadStatistics = this.coreManager.create(ThreadStatisticsObject.class);

        threadStatistics.setBase(this);
        threadStatistics.setDefinition(this.definition.getStatistics());

        return threadStatistics;
    }


}
