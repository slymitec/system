package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.lang.*;
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
public class ProcessHandleTableResolver extends APrototype implements IInfoResolver {
    public ProcessHandleTableResolver() {
        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            ProcessHandleEntryObject processHandleEntry = process.getHandleTable().getEntry(info.getID(), status);
            handle = processHandleEntry.add(openAttribute);

            return handle;
        };

        this.close = (info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            ProcessHandleEntryObject processHandleEntry = process.getHandleTable().getEntry(info.getID(), status);
            processHandleEntry.delete();
        };


        this.readContent = (content, info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrent();

            ProcessHandleEntryObject processHandleEntry = process.getHandleTable().getEntry(info.getID(), status);
            if (!processHandleEntry.isExist()) {
                throw new StatusRelationshipErrorException();
            }

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrent();

            ProcessHandleEntryObject processHandleEntry = process.getHandleTable().getEntry(info.getID(), status);
            if (!processHandleEntry.isExist()) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.executeContent = (info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrent();

            ProcessHandleEntryObject processHandleEntry = process.getHandleTable().getEntry(info.getID(), status);
            if (!processHandleEntry.isExist()) {
                throw new StatusRelationshipErrorException();
            }
        };
    }

    private final OpenFunction open;
    private final CloseConsumer close;
    private final ReadContentFunction readContent;
    private final WriteContentConsumer writeContent;
    private final ExecuteContentConsumer executeContent;

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
