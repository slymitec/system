package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.values.SignalType;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateNotifyParentResolver extends AResolver implements IProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateNotifyParentResolver() {
        this.create = (process, parentProcess, processCreator) -> {
            if (ObjectUtil.allNotNull(parentProcess)) {
                ProcessCommunicationObject parentProcessCommunication = parentProcess.getCommunication();

                if (parentProcessCommunication.isSignalExist()) {
                    parentProcessCommunication.sendSignal(parentProcess.getId(), SignalType.TYPE_PROCESS,
                            LogicalUtil.or(SignalType.ACTION_CREATE, SignalType.RESULT_SUCCESS));
                }
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 5;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(this.create);
    }
}
