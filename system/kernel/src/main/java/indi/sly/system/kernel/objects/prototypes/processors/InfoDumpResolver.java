package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.objects.lang.InfoProcessorDumpFunction;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoDumpResolver extends AInfoResolver {
    public InfoDumpResolver() {
        this.dump = (dump, info, type, cache) -> {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessTokenObject processToken = process.getToken();

            DateTimeObject dateTime = this.coreManager.getDateTime();
            long nowDateTime = dateTime.getCurrentDateTime();
            dump.getDate().put(DateTimeType.CREATE, nowDateTime);
            dump.getDate().put(DateTimeType.ACCESS, nowDateTime);

            dump.setProcessID(process.getId());
            dump.setAccountID(processToken.getAccountId());
            dump.setPath(cache.getPath());

            ProcessInfoTableObject processInfoTable = process.getInfoTable();
            if (processInfoTable.containById(info.getId())) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(info.getId());
                dump.setInfoOpen(processInfoEntry.getOpen());
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
