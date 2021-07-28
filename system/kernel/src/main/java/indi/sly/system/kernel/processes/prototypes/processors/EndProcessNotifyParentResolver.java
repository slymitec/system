package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.instances.values.SignalType;
import indi.sly.system.kernel.processes.lang.EndProcessFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeCycleProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EndProcessNotifyParentResolver extends APrototype implements IProcessKillerResolver {
    private final EndProcessFunction endProcessFunction;

    public EndProcessNotifyParentResolver() {
        this.endProcessFunction = (process, parentProcess) -> {
            if (ObjectUtil.allNotNull(parentProcess)) {
                ProcessCommunicationObject parentProcessCommunication = parentProcess.getCommunication();

                parentProcessCommunication.sendSignal(parentProcess, SignalType.TYPE_PROCESS,
                        LogicalUtil.or(SignalType.ACTION_DELETE, SignalType.RESULT_SUCCESS));
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void resolve(ProcessLifeCycleProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getEnds().add(endProcessFunction);
    }
}
