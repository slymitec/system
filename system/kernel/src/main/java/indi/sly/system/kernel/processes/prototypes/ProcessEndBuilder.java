package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorEndFunction;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessEndBuilder extends ABuilder {
    protected ProcessFactory factory;
    protected ProcessLifeProcessorMediator processorMediator;

    protected ProcessObject process;
    protected ProcessObject parentProcess;

    public synchronized void build() {
        if (ObjectUtil.isAnyNull(this.process)) {
            return;
        }

        ProcessStatusObject processStatus = this.process.getStatus();

        processStatus.die();

        List<ProcessLifeProcessorEndFunction> resolvers = this.processorMediator.getEnds();

        for (ProcessLifeProcessorEndFunction resolver : resolvers) {
            this.process = resolver.apply(this.process, this.parentProcess);
        }

        processStatus.zombie();

        this.process = null;
        this.parentProcess = null;
    }
}
