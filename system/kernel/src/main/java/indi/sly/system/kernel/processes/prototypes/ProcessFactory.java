package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.processors.IProcessResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessFactory extends APrototype {
    protected Set<IProcessResolver> processResolvers;

    public void init() {
        this.processResolvers = new ConcurrentSkipListSet<>();

        Set<APrototype> corePrototypes =
                this.factoryManager.getCoreRepository().getByImplementInterface(SpaceType.KERNEL,
                        IProcessResolver.class);

        for (APrototype prototype : corePrototypes) {
            if (prototype instanceof IProcessResolver) {
                processResolvers.add((IProcessResolver) prototype);
            }
        }
    }

    private ProcessObject buildProcess(ProcessProcessorMediator processorMediator, UUID processID) {
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);

        ProcessObject process = this.factoryManager.create(ProcessObject.class);

        process.factory = this;
        process.processorRegister = processorMediator;
        process.id = processID;
        ProcessStatisticsObject processStatistics = process.getStatistics();
        processStatistics.setDate(DateTimeTypes.ACCESS, dateTime.getCurrentDateTime());

        return process;
    }

    public ProcessObject buildProcess(ProcessEntity process) {
        ProcessProcessorMediator processorMediator = new ProcessProcessorMediator();
        for (IProcessResolver processResolver : this.processResolvers) {
            processResolver.resolve(process, processorMediator);
        }

        return this.buildProcess(processorMediator, process.getID());
    }

    public CreateProcessBuilder createProcessBuilder() {
        CreateProcessBuilder createProcessBuilder = this.factoryManager.create(CreateProcessBuilder.class);

        createProcessBuilder.processFactory = this;

        return createProcessBuilder;
    }
}
