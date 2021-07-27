package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.lang.DumpFunction;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DumpResolver extends APrototype implements IInfoResolver {
    public DumpResolver() {
        this.dump = (dump, info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            dump.getDate().put(DateTimeType.CREATE, nowDateTime);

            dump.getIdentifications().addAll(status.getIdentifications());

            ProcessHandleEntryObject processHandleEntry = process.getHandleTable().getEntry(info.getID(), status);
            if (processHandleEntry.isExist()) {
                dump.setOpen(processHandleEntry.getOpen());
            } else {
                dump.setOpen(null);
            }
            return dump;
        };
    }

    private final DumpFunction dump;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getDumps().add(this.dump);
    }

    @Override
    public int order() {
        return 2;
    }
}
