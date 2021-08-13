package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.prototypes.processors.*;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessFactory extends AFactory {
    protected List<IProcessResolver> processResolvers;
    protected List<IProcessCreateResolver> processCreatorResolvers;
    protected List<IProcessEndResolver> processEndResolvers;

    @Override
    public void init() {
        this.processResolvers = new CopyOnWriteArrayList<>();
        this.processCreatorResolvers = new CopyOnWriteArrayList<>();
        this.processEndResolvers = new CopyOnWriteArrayList<>();

        Set<AResolver> resolvers = new HashSet<>();
        resolvers.add(this.factoryManager.create(ProcessCreateCheckResolver.class));
        resolvers.add(this.factoryManager.create(ProcessCreateCommunicationResolver.class));
        resolvers.add(this.factoryManager.create(ProcessCreateContextResolver.class));
        resolvers.add(this.factoryManager.create(ProcessCreateInfoTableResolver.class));
        resolvers.add(this.factoryManager.create(ProcessCreateNotifyParentResolver.class));
        resolvers.add(this.factoryManager.create(ProcessCreateSessionResolver.class));
        resolvers.add(this.factoryManager.create(ProcessCreateStatisticsResolver.class));
        resolvers.add(this.factoryManager.create(ProcessCreateTokenResolver.class));
        resolvers.add(this.factoryManager.create(ProcessCreateTokenRuleResolver.class));
        resolvers.add(this.factoryManager.create(ProcessEndCommunicationResolver.class));
        resolvers.add(this.factoryManager.create(ProcessEndInfoTableResolver.class));
        resolvers.add(this.factoryManager.create(ProcessEndNotifyParentResolver.class));
        resolvers.add(this.factoryManager.create(ProcessMemberResolver.class));
        resolvers.add(this.factoryManager.create(ProcessSelfResolver.class));
        resolvers.add(this.factoryManager.create(ProcessStatisticsResolver.class));

        for (AResolver resolver : resolvers) {
            if (resolver instanceof IProcessResolver) {
                this.processResolvers.add((IProcessResolver) resolver);
            } else if (resolver instanceof IProcessCreateResolver) {
                this.processCreatorResolvers.add((IProcessCreateResolver) resolver);
            } else if (resolver instanceof IProcessEndResolver) {
                this.processEndResolvers.add((IProcessEndResolver) resolver);
            }
        }

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
