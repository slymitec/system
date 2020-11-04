package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.processors.IProcessObjectProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessObjectFactoryObject extends ACoreObject {
    protected Set<IProcessObjectProcessor> processObjectProcessors;

    public void initProcessObjectFactory() {
        this.processObjectProcessors = new ConcurrentSkipListSet<>();

        Set<ACoreObject> coreObjects = this.factoryManager.getCoreObjectRepository().getByImplementInterface(SpaceTypes.KERNEL, IProcessObjectProcessor.class);

        for (ACoreObject pair : coreObjects) {
            if (pair instanceof IProcessObjectProcessor) {
                processObjectProcessors.add((IProcessObjectProcessor) pair);
            }
        }
    }

    public ProcessObject buildProcessObject(ProcessEntity process) {
        ProcessObjectProcessorRegister processObjectProcessorRegister = new ProcessObjectProcessorRegister();
        for (IProcessObjectProcessor pair : this.processObjectProcessors) {
            pair.postProcess(process, processObjectProcessorRegister);
        }

        ProcessObject processObject = this.factoryManager.create(ProcessObject.class);
        processObject.processorRegister = processObjectProcessorRegister;
        processObject.id = process.getID();

        return processObject;
    }
}
