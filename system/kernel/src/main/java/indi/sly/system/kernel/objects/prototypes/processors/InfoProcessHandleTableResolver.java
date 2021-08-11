package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.objects.lang.*;
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
public class InfoProcessHandleTableResolver extends AResolver implements IInfoResolver {
    public InfoProcessHandleTableResolver() {
        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessHandleTableObject processHandleTable = process.getHandleTable();

            if (processHandleTable.containByInfoID(info.getID())) {
                throw new StatusAlreadyFinishedException();
            }

            processHandleTable.add(info.getID(), status, openAttribute);
            ProcessHandleEntryObject processHandleEntry = processHandleTable.getByInfoID(info.getID());

            return processHandleEntry.getHandle();
        };

        this.close = (info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessHandleTableObject processHandleTable = process.getHandleTable();

            if (!processHandleTable.containByInfoID(info.getID())) {
                throw new StatusAlreadyFinishedException();
            }

            ProcessHandleEntryObject processHandleEntry = processHandleTable.getByInfoID(info.getID());

            processHandleTable.delete(processHandleEntry.getHandle());
        };


        this.readContent = (content, info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessHandleTableObject processHandleTable = process.getHandleTable();

            if (!processHandleTable.containByInfoID(info.getID())) {
                throw new StatusNotReadyException();
            }

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessHandleTableObject processHandleTable = process.getHandleTable();

            if (!processHandleTable.containByInfoID(info.getID())) {
                throw new StatusNotReadyException();
            }
        };

        this.executeContent = (info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessHandleTableObject processHandleTable = process.getHandleTable();

            if (!processHandleTable.containByInfoID(info.getID())) {
                throw new StatusNotReadyException();
            }
        };
    }

    private final InfoProcessorOpenFunction open;
    private final InfoProcessorCloseConsumer close;
    private final InfoProcessorReadContentFunction readContent;
    private final InfoProcessorWriteContentConsumer writeContent;
    private final InfoProcessorExecuteContentConsumer executeContent;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getOpens().add(this.open);
        processorMediator.getCloses().add(this.close);
        processorMediator.getReadContents().add(this.readContent);
        processorMediator.getWriteContents().add(this.writeContent);
        processorMediator.getExecuteContents().add(this.executeContent);
    }

    @Override
    public int order() {
        return 1;
    }
}
