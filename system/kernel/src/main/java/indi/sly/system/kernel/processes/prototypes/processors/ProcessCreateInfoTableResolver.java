package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateInfoTableResolver extends AProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateInfoTableResolver() {
        this.create = (process, parentProcess, processCreator) -> {
            if (!ValueUtil.isAnyNullOrEmpty(processCreator.getFileIndex())) {
                ProcessInfoTableObject processInfoTable = process.getInfoTable();

                ProcessInfoTableObject parentProcessInfoTable = parentProcess.getInfoTable();
                ProcessInfoEntryObject processInfoEntry = parentProcessInfoTable.getByIndex(processCreator.getFileIndex());
                processInfoEntry.setUnsupportedDelete(true);

                processInfoTable.inherit(processCreator.getFileIndex());
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 4;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(create);
    }
}
