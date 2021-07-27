package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.KillProcessFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeCycleProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KillProcessCommunicationResolver extends APrototype implements IProcessKillerResolver {
    private final KillProcessFunction killProcessFunction;

    public KillProcessCommunicationResolver() {
        this.killProcessFunction = (process, parentProcess) -> {
            ProcessCommunicationObject processCommunication = process.getCommunication();

            processCommunication.deleteAllPort();
            processCommunication.deleteSignal();

            return process;
        };
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void resolve(ProcessLifeCycleProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getKills().add(killProcessFunction);
    }
}
