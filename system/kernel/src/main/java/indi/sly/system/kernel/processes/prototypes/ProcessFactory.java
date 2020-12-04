package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.processors.IProcessProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessFactory extends ACorePrototype {
    protected Set<IProcessProcessor> processProcessors;

    public void initProcessObjectFactory() {
        this.processProcessors = new ConcurrentSkipListSet<>();

        Set<ACorePrototype> corePrototypes =
                this.factoryManager.getCoreRepository().getByImplementInterface(SpaceTypes.KERNEL,
                        IProcessProcessor.class);

        for (ACorePrototype pair : corePrototypes) {
            if (pair instanceof IProcessProcessor) {
                processProcessors.add((IProcessProcessor) pair);
            }
        }
    }

    public ProcessObject buildProcessObject(ProcessEntity process) {
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceTypes.KERNEL,
                DateTimeObject.class);

        ProcessProcessorRegister processProcessorRegister = new ProcessProcessorRegister();
        for (IProcessProcessor processObjectProcessor : this.processProcessors) {
            processObjectProcessor.process(process, processProcessorRegister);
        }

        ProcessObject processObject = this.factoryManager.create(ProcessObject.class);

        processObject.factory = this;
        processObject.processorRegister = processProcessorRegister;
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
