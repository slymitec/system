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
import java.util.concurrent.ConcurrentSkipListSet;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessFactory extends APrototype {
    protected Set<IProcessResolver> processProcessors;

    public void init() {
        this.processProcessors = new ConcurrentSkipListSet<>();

        Set<APrototype> corePrototypes =
                this.factoryManager.getCoreRepository().getByImplementInterface(SpaceType.KERNEL,
                        IProcessResolver.class);

        for (APrototype pair : corePrototypes) {
            if (pair instanceof IProcessResolver) {
                processProcessors.add((IProcessResolver) pair);
            }
        }
    }

    public ProcessObject buildProcessObject(ProcessEntity process) {
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);

        ProcessProcessorMediator processProcessorMediator = new ProcessProcessorMediator();
        for (IProcessResolver processObjectProcessor : this.processProcessors) {
            processObjectProcessor.process(process, processProcessorMediator);
        }

        ProcessObject processObject = this.factoryManager.create(ProcessObject.class);

        processObject.factory = this;
        processObject.processorRegister = processProcessorMediator;
        processObject.id = process.getID();
        ProcessStatisticsObject processStatistics = processObject.getStatistics();
        processStatistics.setDate(DateTimeTypes.ACCESS, dateTime.getCurrentDateTime());

        return processObject;
    }

    public CreateProcessBuilder createProcessBuilder() {
        CreateProcessBuilder createProcessBuilder = this.factoryManager.create(CreateProcessBuilder.class);

        createProcessBuilder.setProcessObjectBuilder(this);

        return createProcessBuilder;
    }
}
