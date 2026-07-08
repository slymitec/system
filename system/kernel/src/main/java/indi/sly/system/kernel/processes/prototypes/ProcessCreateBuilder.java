package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.processes.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateBuilder extends ABuilder {
    protected ProcessFactory factory;
    protected ProcessLifeProcessorMediator processorMediator;

    protected ProcessObject parentProcess;
    protected ProcessObject process;

    private void create() {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

        ProcessEntity process = new ProcessEntity();

        process.setId(UUIDUtil.createRandom());
        process.setStatus(ProcessStatusType.NULL);
        if (ObjectUtil.allNotNull(this.parentProcess)) {
            process.setParentProcessID(parentProcess.getId());
        }
        process.setCommunication(new ProcessCommunicationEntity());
        process.setContext(new ProcessContextEntity());
        process.setInfoTable(new ProcessInfoTableEntity());
        process.setSession(new ProcessSessionEntity());
        process.setStatistics(new ProcessStatisticsEntity());
        process.setToken(new ProcessTokenEntity());

        process = processRepository.add(process);

        this.process = this.factory.buildProcess(process);
    }

    public synchronized ProcessObject build(ProcessCreatorRecord processCreator) {
        if (ObjectUtil.isAnyNull(processCreator)) {
            throw new ConditionParametersException();
        }

        if (ObjectUtil.allNotNull(this.process)) {
            return this.process;
        }

        this.create();

        ProcessStatusObject processStatus = this.process.getStatus();
        processStatus.initialize();

        List<ProcessLifeProcessorCreateFunction> resolvers = this.processorMediator.getCreates();

        for (ProcessLifeProcessorCreateFunction resolver : resolvers) {
            this.process = resolver.apply(this.process, this.parentProcess, processCreator);
        }

        processStatus.run();

        return this.process;
    }
}
