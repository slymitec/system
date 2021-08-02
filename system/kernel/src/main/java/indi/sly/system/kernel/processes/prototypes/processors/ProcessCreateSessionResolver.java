package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessSessionObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateSessionResolver extends APrototype implements IProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateSessionResolver() {
        this.create = (process, parentProcess, processCreator) -> {
            ProcessSessionObject processSession = process.getSession();
            if (!ValueUtil.isAnyNullOrEmpty(processCreator.getSessionID())) {
                processSession.setID(processCreator.getSessionID());
            } else {
                processSession.inheritID();
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(create);
    }
}
