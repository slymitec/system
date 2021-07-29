package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.CreateProcessFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessStatisticsObject;
import indi.sly.system.kernel.processes.prototypes.ProcessStatusObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateProcessStatisticsResolver extends APrototype implements IProcessCreatorResolver {
    private final CreateProcessFunction createProcessFunction;

    public CreateProcessStatisticsResolver() {
        this.createProcessFunction = (process, parentProcess, processCreator) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            ProcessStatusObject processStatus = process.getStatus();
            processStatus.initialize();

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
        processorCreatorMediator.getCreates().add(createProcessFunction);
    }
}
