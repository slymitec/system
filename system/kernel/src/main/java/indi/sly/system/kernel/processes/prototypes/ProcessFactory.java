package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.prototypes.processors.IProcessCreatorResolver;
import indi.sly.system.kernel.processes.prototypes.processors.IProcessKillerResolver;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeCycleProcessorMediator;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessCreatorDefinition;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.processors.IProcessResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessFactory extends APrototype {
    protected Set<IProcessResolver> processResolvers;
    protected List<IProcessCreatorResolver> processCreatorResolvers;
    protected List<IProcessKillerResolver> processKillerResolvers;

    public void init() {
        this.processResolvers = new ConcurrentSkipListSet<>();
        this.processCreatorResolvers = new CopyOnWriteArrayList<>();
        this.processKillerResolvers = new CopyOnWriteArrayList<>();

        Set<APrototype> corePrototypes =
                this.factoryManager.getCoreRepository().getByImplementInterface(SpaceType.KERNEL, IProcessResolver.class);

        for (APrototype prototype : corePrototypes) {
            if (prototype instanceof IProcessResolver) {
                processResolvers.add((IProcessResolver) prototype);
            } else if (prototype instanceof IProcessCreatorResolver) {
                processCreatorResolvers.add((IProcessCreatorResolver) prototype);
            } else if (prototype instanceof IProcessKillerResolver) {
                processKillerResolvers.add((IProcessKillerResolver) prototype);
            }
        }

        Collections.sort(processCreatorResolvers);
        Collections.sort(processKillerResolvers);
    }

    private ProcessObject buildProcess(ProcessProcessorMediator processorMediator, UUID processID) {
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);

        ProcessObject process = this.factoryManager.create(ProcessObject.class);

        process.factory = this;
        process.processorMediator = processorMediator;
        process.id = processID;
        ProcessStatisticsObject processStatistics = process.getStatistics();
        processStatistics.setDate(DateTimeType.ACCESS, dateTime.getCurrentDateTime());

        return process;
    }

    public ProcessObject buildProcess(ProcessEntity process) {
        ProcessProcessorMediator processorMediator = this.factoryManager.create(ProcessProcessorMediator.class);
        for (IProcessResolver processResolver : this.processResolvers) {
            processResolver.resolve(process, processorMediator);
        }

        return this.buildProcess(processorMediator, process.getID());
    }

    public ProcessCreatorBuilder createProcess(ProcessObject parentProcess, ProcessCreatorDefinition processCreator) {
        if (ObjectUtil.isAnyNull(processCreator)) {
            throw new ConditionParametersException();
        }

        ProcessLifeCycleProcessorMediator processorMediator = this.factoryManager.create(ProcessLifeCycleProcessorMediator.class);
        for (IProcessCreatorResolver processCreatorResolver : this.processCreatorResolvers) {
            processCreatorResolver.resolve(processorMediator);
        }

        ProcessCreatorBuilder processCreatorBuilder = this.factoryManager.create(ProcessCreatorBuilder.class);

        processCreatorBuilder.processorMediator = processorMediator;
        processCreatorBuilder.factory = this;
        processCreatorBuilder.processCreator = processCreator;
        processCreatorBuilder.parentProcess = parentProcess;

        return processCreatorBuilder;
    }

    public ProcessKillerBuilder killProcess(ProcessObject parentProcess, ProcessObject process) {
        if (ObjectUtil.isAnyNull(process)) {
            throw new ConditionParametersException();
        }

        ProcessLifeCycleProcessorMediator processorMediator = this.factoryManager.create(ProcessLifeCycleProcessorMediator.class);
        for (IProcessKillerResolver processKillerResolver : this.processKillerResolvers) {
            processKillerResolver.resolve(processorMediator);
        }

        ProcessKillerBuilder processKillerBuilder = this.factoryManager.create(ProcessKillerBuilder.class);

        processKillerBuilder.processorMediator = processorMediator;
        processKillerBuilder.factory = this;
        processKillerBuilder.parentProcess = parentProcess;
        processKillerBuilder.process = process;

        return processKillerBuilder;
    }
}
