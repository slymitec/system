package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.CreateProcessFunction;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessCreatorProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessCreatorDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BuildProcessObjectResolver extends APrototype implements IProcessCreatorResolver {
    private final CreateProcessFunction createProcessFunction;

    public BuildProcessObjectResolver() {
        this.createProcessFunction = (process, parentProcess, processCreator) -> {
            return process;
        };
    }

    @Override
    public void resolve(ProcessCreatorDefinition processCreator, ProcessCreatorProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(createProcessFunction);
    }
}
