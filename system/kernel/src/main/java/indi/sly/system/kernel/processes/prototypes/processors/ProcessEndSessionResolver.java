package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorEndFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessSessionObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessEndSessionResolver extends AProcessEndResolver {
    private final ProcessLifeProcessorEndFunction end;

    public ProcessEndSessionResolver() {
        this.end = (process, parentProcess) -> {
            ProcessSessionObject processSession = process.getSession();

            if (!ValueUtil.isAnyNullOrEmpty(processSession.getID())) {
                processSession.close();
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getEnds().add(end);
    }
}
