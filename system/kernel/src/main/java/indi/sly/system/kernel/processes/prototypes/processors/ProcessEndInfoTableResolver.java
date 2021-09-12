package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorEndFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessEndInfoTableResolver extends AProcessEndResolver {
    private final ProcessLifeProcessorEndFunction end;

    public ProcessEndInfoTableResolver() {
        this.end = (process, parentProcess) -> {
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            for (UUID index : processInfoTable.list()) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getByIndex(index);

                if (processInfoEntry.isUnsupportedDelete()) {
                    processInfoEntry.setUnsupportedDelete(false);
                }

                try {
                    InfoObject info = processInfoEntry.getInfo();
                    info.close();
                } catch (AKernelException ignored) {
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
