package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorEndFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleTableObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessEndHandleTableResolver extends APrototype implements IProcessEndResolver {
    private final ProcessLifeProcessorEndFunction end;

    public ProcessEndHandleTableResolver() {
        this.end = (process, parentProcess) -> {
            ProcessHandleTableObject processHandleTable = process.getHandleTable();

            for (UUID handle : processHandleTable.list()) {
                ProcessHandleEntryObject processHandleEntry = processHandleTable.getByHandle(handle);

                try {
                    InfoObject info = processHandleEntry.getInfo();
                    info.close();
                } catch (AKernelException ignore) {
                }
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
