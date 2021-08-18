package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.processes.values.ThreadDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadObject extends AObject {
    public ThreadObject() {
        this.thread = new ThreadDefinition();
    }

    private final ThreadDefinition thread;

    public UUID getID() {
        return this.thread.getID();
    }

    public UUID getProcessID() {
        return this.thread.getProcessID();
    }

    public void setProcessID(UUID id) {
        this.thread.setProcessID(id);
    }

    public synchronized ThreadStatusObject getStatus() {
        ThreadStatusObject threadStatus = this.factoryManager.create(ThreadStatusObject.class);

        threadStatus.setParent(this);
        threadStatus.setSource(() -> this.thread, (source) -> {
        });

        return threadStatus;
    }

    public synchronized ThreadContextObject getContext() {
        ThreadContextObject threadContext = this.factoryManager.create(ThreadContextObject.class);

        threadContext.setParent(this);
        threadContext.setSource(this.thread::getContext, this.thread::setContext);

        return threadContext;
    }

    public synchronized ThreadStatisticsObject getStatistics() {
        ThreadStatisticsObject threadStatistics = this.factoryManager.create(ThreadStatisticsObject.class);

        threadStatistics.setParent(this);
        threadStatistics.setSource(this.thread::getStatistics, this.thread::setStatistics);

        return threadStatistics;
    }


}
