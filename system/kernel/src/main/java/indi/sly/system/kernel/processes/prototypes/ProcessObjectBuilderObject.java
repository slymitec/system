package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.processors.IProcessObjectProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessObjectBuilderObject extends ACorePrototype {
    protected Set<IProcessObjectProcessor> processObjectProcessors;

    public void initProcessObjectFactory() {
        this.processObjectProcessors = new ConcurrentSkipListSet<>();

        Set<ACorePrototype> coreObjects =
                this.factoryManager.getCoreRepository().getByImplementInterface(SpaceTypes.KERNEL,
                        IProcessObjectProcessor.class);

        for (ACorePrototype pair : coreObjects) {
            if (pair instanceof IProcessObjectProcessor) {
                processObjectProcessors.add((IProcessObjectProcessor) pair);
            }
        }
    }

    public ProcessObject buildProcessObject(ProcessEntity process) {
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceTypes.KERNEL,
                DateTimeObject.class);

        ProcessObjectProcessorRegister processObjectProcessorRegister = new ProcessObjectProcessorRegister();
        for (IProcessObjectProcessor processObjectProcessor : this.processObjectProcessors) {
            processObjectProcessor.process(process, processObjectProcessorRegister);
        }

        ProcessObject processObject = this.factoryManager.create(ProcessObject.class);

        processObject.factory = this;
        processObject.processorRegister = processObjectProcessorRegister;
        processObject.id = process.getID();
        ProcessStatisticsObject processStatistics = processObject.getStatistics();
        processStatistics.setDate(DateTimeTypes.ACCESS, dateTime.getCurrentDateTime());

        return processObject;
    }

    public ProcessBuilderObject createProcessBuilder() {
        ProcessBuilderObject processBuilder = this.factoryManager.create(ProcessBuilderObject.class);

        processBuilder.setProcessObjectBuilder(this);

        return processBuilder;
    }
}
