package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.lang.CreateProcessFunction;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.processes.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreatorBuilder extends APrototype {
    protected ProcessFactory factory;
    protected ProcessLifeProcessorMediator processorMediator;
    protected ProcessCreatorDefinition processCreator;

    protected ProcessObject parentProcess;

    private ProcessObject init() {
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
        process.setContext(ObjectUtil.transferToByteArray(new ProcessCommunicationDefinition()));
        process.setHandleTable(ObjectUtil.transferToByteArray(new ProcessHandleTableDefinition()));
        process.setStatistics(ObjectUtil.transferToByteArray(new ProcessStatisticsDefinition()));
        process.setToken(ObjectUtil.transferToByteArray(new ProcessTokenDefinition()));

        processRepository.add(process);

        return this.factory.buildProcess(process);
    }

    public ProcessObject build() {
        ProcessObject process = this.init();

        ProcessStatusObject processStatus = process.getStatus();

        processStatus.initialize();

        List<CreateProcessFunction> resolvers = this.processorMediator.getCreates();

        for (CreateProcessFunction resolver : resolvers) {
            process = resolver.apply(process, this.parentProcess, this.processCreator);
        }

        processStatus.run();

        return process;
    }
}
