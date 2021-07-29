package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.CreateProcessFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleTableObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateProcessHandleTableResolver extends APrototype implements IProcessCreatorResolver {
    private final CreateProcessFunction createProcessFunction;

    public CreateProcessHandleTableResolver() {
        this.createProcessFunction = (process, parentProcess, processCreator) -> {
            if (!ValueUtil.isAnyNullOrEmpty(processCreator.getFileHandle())) {
                ProcessHandleTableObject processHandleTable = process.getHandleTable();
                processHandleTable.inherit(processCreator.getFileHandle());
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 3;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(createProcessFunction);
    }
}
