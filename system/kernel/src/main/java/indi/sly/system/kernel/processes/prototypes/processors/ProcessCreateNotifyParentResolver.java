package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.processes.instances.values.SignalType;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateNotifyParentResolver extends AProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateNotifyParentResolver() {
        this.create = (process, parentProcess, processCreator) -> {
            if (ObjectUtil.allNotNull(parentProcess)) {
                ProcessCommunicationObject parentProcessCommunication = parentProcess.getCommunication();

                UUID signalID = parentProcessCommunication.getSignalID();

                if (!ValueUtil.isAnyNullOrEmpty(signalID)) {
                    parentProcessCommunication.sendSignal(signalID, SignalType.TYPE_PROCESS,
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
        processorCreatorMediator.getCreates().add(create);
    }
}
