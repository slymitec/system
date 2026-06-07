package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.ASystemException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.values.SignalType;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorEndFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessEndNotifyParentResolver extends AResolver implements IProcessEndResolver {
    private final ProcessLifeProcessorEndFunction end;

    public ProcessEndNotifyParentResolver() {
        this.end = (process, parentProcess) -> {
            if (ObjectUtil.allNotNull(parentProcess)) {
                ProcessCommunicationObject parentProcessCommunication = parentProcess.getCommunication();
                ProcessCommunicationObject processCommunication = process.getCommunication();

                if (parentProcessCommunication.isSignalExist()) {
                    try {
                        processCommunication.sendSignal(parentProcess.getId(), SignalType.TYPE_PROCESS,
                                LogicalUtil.or(SignalType.ACTION_DELETE, SignalType.RESULT_SUCCESS));
                    } catch (ASystemException ignored) {
                    }
                }
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
        processorCreatorMediator.getEnds().add(this.end);
    }
}
