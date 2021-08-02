package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleTableObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateHandleTableResolver extends APrototype implements IProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateHandleTableResolver() {
        this.create = (process, parentProcess, processCreator) -> {
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
        processorCreatorMediator.getCreates().add(create);
    }
}
