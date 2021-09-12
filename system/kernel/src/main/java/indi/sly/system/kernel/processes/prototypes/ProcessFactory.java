package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.processes.prototypes.processors.*;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessFactory extends AFactory {
    public ProcessFactory(){
        this.processResolvers = new CopyOnWriteArrayList<>();
        this.processCreatorResolvers = new CopyOnWriteArrayList<>();
        this.processEndResolvers = new CopyOnWriteArrayList<>();
    }

    protected final List<AProcessResolver> processResolvers;
    protected final List<AProcessCreateResolver> processCreatorResolvers;
    protected final List<AProcessEndResolver> processEndResolvers;

    @Override
    public void init() {
        this.processCreatorResolvers.add(this.factoryManager.create(ProcessCreateCheckResolver.class));
        this.processCreatorResolvers.add(this.factoryManager.create(ProcessCreateContextResolver.class));
        this.processCreatorResolvers.add(this.factoryManager.create(ProcessCreateInfoTableResolver.class));
        this.processCreatorResolvers.add(this.factoryManager.create(ProcessCreateNotifyParentResolver.class));
        this.processCreatorResolvers.add(this.factoryManager.create(ProcessCreateSessionResolver.class));
        this.processCreatorResolvers.add(this.factoryManager.create(ProcessCreateStatisticsResolver.class));
        this.processCreatorResolvers.add(this.factoryManager.create(ProcessCreateTokenResolver.class));
        this.processCreatorResolvers.add(this.factoryManager.create(ProcessCreateTokenRuleResolver.class));
        this.processEndResolvers.add(this.factoryManager.create(ProcessEndCommunicationResolver.class));
        this.processEndResolvers.add(this.factoryManager.create(ProcessEndInfoTableResolver.class));
        this.processEndResolvers.add(this.factoryManager.create(ProcessEndNotifyParentResolver.class));
        this.processResolvers.add(this.factoryManager.create(ProcessMemberResolver.class));
        this.processResolvers.add(this.factoryManager.create(ProcessSelfResolver.class));
        this.processResolvers.add(this.factoryManager.create(ProcessStatisticsResolver.class));

        Collections.sort(this.processResolvers);
        Collections.sort(this.processCreatorResolvers);
        Collections.sort(this.processEndResolvers);
    }

    private ProcessObject buildProcess(ProcessProcessorMediator processorMediator, UUID processID) {
        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);

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
        for (AProcessResolver processResolver : this.processResolvers) {
            processResolver.resolve(process, processorMediator);
        }

        return this.buildProcess(processorMediator, process.getID());
    }

    public ProcessCreateBuilder createProcessCreator(ProcessObject parentProcess) {
        ProcessLifeProcessorMediator processorMediator = this.factoryManager.create(ProcessLifeProcessorMediator.class);
        for (AProcessCreateResolver processCreatorResolver : this.processCreatorResolvers) {
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
        for (AProcessEndResolver processEndResolver : this.processEndResolvers) {
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
