package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.instances.values.SignalType;
import indi.sly.system.kernel.processes.lang.CreateProcessFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeCycleProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateProcessNotifyParentResolver extends APrototype implements IProcessCreatorResolver {
    private final CreateProcessFunction createProcessFunction;

    public CreateProcessNotifyParentResolver() {
        this.createProcessFunction = (process, parentProcess, processCreator) -> {
            if (ObjectUtil.allNotNull(parentProcess)) {
                ProcessCommunicationObject parentProcessCommunication = parentProcess.getCommunication();

                parentProcessCommunication.sendSignal(parentProcess, SignalType.TYPE_PROCESS,
                        LogicalUtil.or(SignalType.ACTION_CREATE, SignalType.RESULT_SUCCESS));
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 4;
    }

    @Override
    public void resolve(ProcessLifeCycleProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(createProcessFunction);
    }
}
