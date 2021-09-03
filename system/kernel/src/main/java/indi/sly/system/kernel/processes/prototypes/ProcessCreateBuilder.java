package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.processes.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateBuilder extends ABuilder {
    protected ProcessFactory factory;
    protected ProcessLifeProcessorMediator processorMediator;

    protected ProcessObject parentProcess;
    protected ProcessObject process;

    private void build() {
        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

        ProcessEntity process = new ProcessEntity();

        process.setID(UUIDUtil.createRandom());
        process.setStatus(ProcessStatusType.NULL);
        if (ObjectUtil.allNotNull(this.parentProcess)) {
            process.setParentProcessID(parentProcess.getID());
        }
        process.setSessionID(UUIDUtil.getEmpty());
        process.setCommunication(ObjectUtil.transferToByteArray(new ProcessCommunicationDefinition()));
        process.setContext(ObjectUtil.transferToByteArray(new ProcessContextDefinition()));
        process.setInfoTable(ObjectUtil.transferToByteArray(new ProcessInfoTableDefinition()));
        process.setStatistics(ObjectUtil.transferToByteArray(new ProcessStatisticsDefinition()));
        process.setToken(ObjectUtil.transferToByteArray(new ProcessTokenDefinition()));

        processRepository.add(process);

        this.process = this.factory.buildProcess(process);
    }

    public ProcessObject build(ProcessCreatorDefinition processCreator) {
        if (ObjectUtil.isAnyNull(processCreator)) {
            throw new ConditionParametersException();
        }

        this.build();

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
