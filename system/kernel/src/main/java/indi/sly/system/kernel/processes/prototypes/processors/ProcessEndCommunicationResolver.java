package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorEndFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessEndCommunicationResolver extends AProcessEndResolver {
    private final ProcessLifeProcessorEndFunction end;

    public ProcessEndCommunicationResolver() {
        this.end = (process, parentProcess) -> {
            ProcessCommunicationObject processCommunication = process.getCommunication();

            processCommunication.deleteAllPort();
            if (!ValueUtil.isAnyNullOrEmpty(processCommunication.getSignalID())) {
                processCommunication.deleteSignal();
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
