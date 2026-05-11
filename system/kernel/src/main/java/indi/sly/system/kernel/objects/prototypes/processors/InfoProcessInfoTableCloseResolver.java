package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.objects.lang.InfoProcessorCloseFunction;
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
public class InfoProcessInfoTableCloseResolver extends AResolver implements IInfoResolver {
    public InfoProcessInfoTableCloseResolver() {
        this.close = (info, type, cache) -> {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            if (!processInfoTable.containById(info.getId())) {
                throw new StatusAlreadyFinishedException();
            }

            ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(info.getId());

            processInfoEntry.delete();

            return info;
        };
    }

    private final InfoProcessorCloseFunction close;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getCloses().add(this.close);
    }

    @Override
    public int order() {
        return 3;
    }
}
