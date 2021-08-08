package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.prototypes.processors.IProcessCreateResolver;
import indi.sly.system.kernel.processes.prototypes.processors.IProcessEndResolver;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
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
public class ProcessFactory extends AFactory {
    protected Set<IProcessResolver> processResolvers;
    protected List<IProcessCreateResolver> processCreatorResolvers;
    protected List<IProcessEndResolver> processEndResolvers;

    @Override
    public void init() {
        this.processResolvers = new ConcurrentSkipListSet<>();
        this.processCreatorResolvers = new CopyOnWriteArrayList<>();
        this.processEndResolvers = new CopyOnWriteArrayList<>();

        Set<APrototype> corePrototypes =
                this.factoryManager.getCorePrototypeRepository().getByImplementInterface(SpaceType.KERNEL, IProcessResolver.class);

        for (APrototype prototype : corePrototypes) {
            if (prototype instanceof IProcessResolver) {
                this.processResolvers.add((IProcessResolver) prototype);
            } else if (prototype instanceof IProcessCreateResolver) {
                this.processCreatorResolvers.add((IProcessCreateResolver) prototype);
            } else if (prototype instanceof IProcessEndResolver) {
                this.processEndResolvers.add((IProcessEndResolver) prototype);
            }
        }

        Collections.sort(this.processCreatorResolvers);
        Collections.sort(this.processEndResolvers);
    }

    private ProcessObject buildProcess(ProcessProcessorMediator processorMediator, UUID processID) {
        DateTimeObject dateTime = this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL,
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

    public ProcessCreateBuilder createProcessCreator(ProcessObject parentProcess) {
        ProcessLifeProcessorMediator processorMediator = this.factoryManager.create(ProcessLifeProcessorMediator.class);
        for (IProcessCreateResolver processCreatorResolver : this.processCreatorResolvers) {
            processCreatorResolver.resolve(processorMediator);
        }

        ProcessCreateBuilder processCreateBuilder = this.factoryManager.create(ProcessCreateBuilder.class);

        processCreateBuilder.processorMediator = processorMediator;
        processCreateBuilder.factory = this;
        processCreateBuilder.parentProcess = parentProcess;

        return processCreateBuilder;
    }

    public ProcessEndBuilder createProcessEnd(ProcessObject parentProcess, ProcessObject process) {
        if (ObjectUtil.isAnyNull(process)) {
            throw new ConditionParametersException();
        }

        ProcessLifeProcessorMediator processorMediator = this.factoryManager.create(ProcessLifeProcessorMediator.class);
        for (IProcessEndResolver processEndResolver : this.processEndResolvers) {
            processEndResolver.resolve(processorMediator);
        }

        ProcessEndBuilder processEndBuilder = this.factoryManager.create(ProcessEndBuilder.class);

        processEndBuilder.processorMediator = processorMediator;
        processEndBuilder.factory = this;
        processEndBuilder.parentProcess = parentProcess;
        processEndBuilder.process = process;

        return processEndBuilder;
    }
}
