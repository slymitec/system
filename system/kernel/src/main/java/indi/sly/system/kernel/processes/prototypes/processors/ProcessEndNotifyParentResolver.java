package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.processes.instances.values.SignalType;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorEndFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessEndNotifyParentResolver extends AProcessEndResolver {
    private final ProcessLifeProcessorEndFunction end;

    public ProcessEndNotifyParentResolver() {
        this.end = (process, parentProcess) -> {
            if (ObjectUtil.allNotNull(parentProcess)) {
                ProcessCommunicationObject parentProcessCommunication = parentProcess.getCommunication();
                ProcessCommunicationObject processCommunication = process.getCommunication();

                UUID signalID = parentProcessCommunication.getSignalID();

                if (!ValueUtil.isAnyNullOrEmpty(signalID)) {
                    try {
                        processCommunication.sendSignal(signalID, SignalType.TYPE_PROCESS,
                                LogicalUtil.or(SignalType.ACTION_DELETE, SignalType.RESULT_SUCCESS));
                    } catch (AKernelException ignored) {
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
