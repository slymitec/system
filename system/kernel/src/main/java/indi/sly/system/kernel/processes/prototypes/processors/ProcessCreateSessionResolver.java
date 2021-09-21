package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessSessionObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateSessionResolver extends AProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateSessionResolver() {
        this.create = (process, parentProcess, processCreator) -> {
            ProcessTokenObject parentProcessToken = parentProcess.getToken();
            ProcessTokenObject processToken = process.getToken();
            ProcessSessionObject parentProcessSession = parentProcess.getSession();
            ProcessSessionObject processSession = process.getSession();

            if (processToken.getAccountID().equals(parentProcessToken.getAccountID())
                    && !ValueUtil.isAnyNullOrEmpty(parentProcessSession.getID())) {
                processSession.inheritID();
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(create);
    }
}
