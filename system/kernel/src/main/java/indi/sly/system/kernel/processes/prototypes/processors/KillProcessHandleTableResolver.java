package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.lang.CreateProcessFunction;
import indi.sly.system.kernel.processes.lang.KillProcessFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleTableObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeCycleProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KillProcessHandleTableResolver extends APrototype implements IProcessKillerResolver {
    private final KillProcessFunction killProcessFunction;

    public KillProcessHandleTableResolver() {
        this.killProcessFunction = (process, parentProcess) -> {
            ProcessHandleTableObject processHandleTable = process.getHandleTable();

            for (UUID handle : processHandleTable.list()) {
                InfoObject info = processHandleTable.get(handle);
                info.close();
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
        processorCreatorMediator.getKills().add(killProcessFunction);
    }
}
