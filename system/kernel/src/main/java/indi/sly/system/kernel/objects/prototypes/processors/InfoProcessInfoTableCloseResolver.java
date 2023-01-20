package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.kernel.objects.lang.InfoProcessorCloseFunction;
import indi.sly.system.kernel.objects.lang.InfoProcessorOpenFunction;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoProcessInfoTableCloseResolver extends AInfoResolver {
    public InfoProcessInfoTableCloseResolver() {
        this.open = (index, info, type, status, openAttribute, arguments) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            if (processInfoTable.containByID(info.getID())) {
                throw new StatusAlreadyFinishedException();
            }

            return index;
        };

        this.close = (info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            if (!processInfoTable.containByID(info.getID())) {
                throw new StatusAlreadyFinishedException();
            }

            ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(info.getID());

            processInfoEntry.delete();

            return info;
        };
    }

    private final InfoProcessorOpenFunction open;
    private final InfoProcessorCloseFunction close;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getOpens().add(this.open);
        processorMediator.getCloses().add(this.close);
    }

    @Override
    public int order() {
        return 3;
    }
}
