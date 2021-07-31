package indi.sly.system.services.center.prototypes.processors;

import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.kernel.processes.prototypes.ThreadStatusObject;
import indi.sly.system.services.center.lang.FinishConsumer;
import indi.sly.system.services.center.lang.StartFunction;
import indi.sly.system.services.center.prototypes.wrappers.CenterProcessorMediator;
import indi.sly.system.services.center.values.CenterDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessesResolver extends APrototype implements ICenterResolver {
    public ProcessesResolver() {
        this.start = (center, status) -> {
            if (!ValueUtil.isAnyNullOrEmpty(center.getProcessID())) {
                ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
                ThreadObject thread = threadManager.create(center.getProcessID());

                ThreadStatusObject threadStatus = thread.getStatus();
                threadStatus.start();
            }
        };

        this.finish = (center, status) -> {
            if (!ValueUtil.isAnyNullOrEmpty(center.getProcessID())) {
                ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
                ThreadObject thread = threadManager.getCurrent();

                ThreadStatusObject threadStatus = thread.getStatus();
                threadStatus.end();
                threadManager.end();
            }
        };
    }

    @Override
    public int order() {
        return 1;
    }

    private final StartFunction start;
    private final FinishConsumer finish;

    @Override
    public void resolve(CenterDefinition center, CenterProcessorMediator processorMediator) {
        if (!ValueUtil.isAnyNullOrEmpty(center.getProcessID())) {
            processorMediator.getStarts().add(this.start);
            processorMediator.getFinishes().add(this.finish);
        }
    }
}
