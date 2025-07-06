package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessStatisticsObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateStatisticsResolver extends AProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateStatisticsResolver() {
        this.create = (process, parentProcess, processCreator) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            processStatistics.setDate(DateTimeType.CREATE, nowDateTime);
            processStatistics.setDate(DateTimeType.ACCESS, nowDateTime);

            return process;
        };
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(this.create);
    }
}
