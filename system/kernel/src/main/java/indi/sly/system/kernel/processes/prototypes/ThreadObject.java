package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.common.lang.Function2;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.values.ThreadDefinition;
import indi.sly.system.kernel.processes.values.ThreadStatisticsDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadObject extends APrototype {
    public ThreadObject() {
        this.thread = new ThreadDefinition();
    }

    private ThreadDefinition thread;

    public UUID getID() {
        return this.thread.getID();
    }

    public UUID getProcessID() {
        return this.thread.getProcessID();
    }

    public ThreadStatisticsObject getStatistics() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getProcess(this.getProcessID());

        ThreadStatisticsObject threadStatistics = this.factoryManager.create(ThreadStatisticsObject.class);

        threadStatistics.setSource(() -> this.thread.getStatistics(), (ThreadStatisticsDefinition source) -> {
            this.thread.setStatistics(source);
        });
        threadStatistics.setProcess(process);

        return threadStatistics;
    }
}
