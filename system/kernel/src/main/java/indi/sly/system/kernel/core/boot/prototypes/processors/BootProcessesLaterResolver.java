package indi.sly.system.kernel.core.boot.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.boot.lang.BootStartConsumer;
import indi.sly.system.kernel.core.boot.prototypes.wrappers.BootProcessorMediator;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashSet;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootProcessesLaterResolver extends ABootResolver {
    public BootProcessesLaterResolver() {
        this.start = (startup) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_AFTER_KERNEL)) {
                ProcessObject process = processManager.getCurrent();

                ProcessCommunicationObject communication = process.getCommunication();

                UUID signalID = communication.getSignalID();
                if (ValueUtil.isAnyNullOrEmpty(signalID)) {
                    communication.createSignal(new HashSet<>());
                }
            }
        };
    }

    private final BootStartConsumer start;

    @Override
    public void resolve(BootProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
    }

    @Override
    public int order() {
        return 2;
    }
}
