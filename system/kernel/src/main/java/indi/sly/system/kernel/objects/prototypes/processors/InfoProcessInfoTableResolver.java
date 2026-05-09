package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.kernel.objects.lang.InfoProcessorExecuteContentConsumer;
import indi.sly.system.kernel.objects.lang.InfoProcessorOpenFunction;
import indi.sly.system.kernel.objects.lang.InfoProcessorReadContentFunction;
import indi.sly.system.kernel.objects.lang.InfoProcessorWriteContentConsumer;
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
public class InfoProcessInfoTableResolver extends AInfoResolver {
    public InfoProcessInfoTableResolver() {
        this.open = (index, info, type, cache, openAttribute, arguments) -> {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            ProcessInfoEntryObject processInfoEntry = processInfoTable.create(info.getId(), cache, openAttribute);

            return processInfoEntry.getIndex();
        };

        this.readContent = (content, info, type, cache) -> {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            if (!processInfoTable.containById(info.getId())) {
                throw new StatusNotReadyException();
            }

            return content;
        };

        this.writeContent = (info, type, cache, content) -> {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            if (!processInfoTable.containById(info.getId())) {
                throw new StatusNotReadyException();
            }
        };

        this.executeContent = (info, type, cache) -> {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            if (!processInfoTable.containById(info.getId())) {
                throw new StatusNotReadyException();
            }
        };
    }

    private final InfoProcessorOpenFunction open;
    private final InfoProcessorReadContentFunction readContent;
    private final InfoProcessorWriteContentConsumer writeContent;
    private final InfoProcessorExecuteContentConsumer executeContent;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getOpens().add(this.open);
        processorMediator.getReadContents().add(this.readContent);
        processorMediator.getWriteContents().add(this.writeContent);
        processorMediator.getExecuteContents().add(this.executeContent);
    }

    @Override
    public int order() {
        return 1;
    }
}
