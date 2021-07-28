package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.lang.EndProcessFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleTableObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeCycleProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EndProcessHandleTableResolver extends APrototype implements IProcessKillerResolver {
    private final EndProcessFunction endProcessFunction;

    public EndProcessHandleTableResolver() {
        this.endProcessFunction = (process, parentProcess) -> {
            ProcessHandleTableObject processHandleTable = process.getHandleTable();

            for (UUID handle : processHandleTable.list()) {
                ProcessHandleEntryObject processHandleEntry = processHandleTable.getByHandle(handle);
                InfoObject info = processHandleEntry.getInfo();
                info.close();
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void resolve(ProcessLifeCycleProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getEnds().add(endProcessFunction);
    }
}
