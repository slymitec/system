package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.lang.InfoProcessorDumpFunction;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoDumpResolver extends APrototype implements IInfoResolver {
    public InfoDumpResolver() {
        this.dump = (dump, info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            DateTimeObject dateTime = this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();
            dump.getDate().put(DateTimeType.CREATE, nowDateTime);

            dump.getIdentifications().addAll(status.getIdentifications());

            ProcessHandleTableObject processHandleTable = process.getHandleTable();
            if (processHandleTable.containByInfoID(info.getID())) {
                ProcessHandleEntryObject processHandleEntry = processHandleTable.getByInfoID(info.getID());
                dump.setInfoOpen(processHandleEntry.getOpen());
            } else {
                dump.setInfoOpen(null);
            }

            return dump;
        };
    }

    private final InfoProcessorDumpFunction dump;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getDumps().add(this.dump);
    }

    @Override
    public int order() {
        return 2;
    }
}
