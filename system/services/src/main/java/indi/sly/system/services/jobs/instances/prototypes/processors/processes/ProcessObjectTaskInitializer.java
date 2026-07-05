package indi.sly.system.services.jobs.instances.prototypes.processors.processes;

import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.*;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandleContextDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessObjectTaskInitializer extends ATaskInitializer {
    public ProcessObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ProcessManager.class).getFactory().rebuildProcess(handle);

        this.register("getId", this::getId, TransactionType.INDEPENDENCE);
        this.register("getParentId", this::getParentId, TransactionType.INDEPENDENCE);
        this.register("isCurrent", this::isCurrent, TransactionType.INDEPENDENCE);
        this.register("getStatus", this::getStatus, TransactionType.INDEPENDENCE);
        this.register("getCommunication", this::getCommunication, TransactionType.INDEPENDENCE);
        this.register("getContext", this::getContext, TransactionType.INDEPENDENCE);
        this.register("getInfoTable", this::getInfoTable, TransactionType.INDEPENDENCE);
        this.register("getSession", this::getSession, TransactionType.INDEPENDENCE);
        this.register("getStatistics", this::getStatistics, TransactionType.INDEPENDENCE);
        this.register("getToken", this::getToken, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getId(TaskRunConsumer run, TaskContentObject content) {
        ProcessObject process = content.getCacheableObject();

        content.setResult(process.getId());
    }

    private void getParentId(TaskRunConsumer run, TaskContentObject content) {
        ProcessObject process = content.getCacheableObject();

        content.setResult(process.getParentId());
    }

    private void isCurrent(TaskRunConsumer run, TaskContentObject content) {
        ProcessObject process = content.getCacheableObject();

        content.setResult(process.isCurrent());
    }

    private void getStatus(TaskRunConsumer run, TaskContentObject content) {
        ProcessObject process = content.getCacheableObject();

        ProcessStatusObject processStatus = process.getStatus();

        UUID handle = processStatus.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(processStatus.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getCommunication(TaskRunConsumer run, TaskContentObject content) {
        ProcessObject process = content.getCacheableObject();

        ProcessCommunicationObject processCommunication = process.getCommunication();

        UUID handle = processCommunication.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(processCommunication.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getContext(TaskRunConsumer run, TaskContentObject content) {
        ProcessObject process = content.getCacheableObject();

        ProcessContextObject processContext = process.getContext();

        UUID handle = processContext.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(processContext.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getInfoTable(TaskRunConsumer run, TaskContentObject content) {
        ProcessObject process = content.getCacheableObject();

        ProcessInfoTableObject processInfoTable = process.getInfoTable();

        UUID handle = processInfoTable.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(processInfoTable.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getSession(TaskRunConsumer run, TaskContentObject content) {
        ProcessObject process = content.getCacheableObject();

        ProcessSessionObject processSession = process.getSession();

        UUID handle = processSession.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(processSession.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getStatistics(TaskRunConsumer run, TaskContentObject content) {
        ProcessObject process = content.getCacheableObject();

        ProcessStatisticsObject processStatistics = process.getStatistics();

        UUID handle = processStatistics.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(processStatistics.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getToken(TaskRunConsumer run, TaskContentObject content) {
        ProcessObject process = content.getCacheableObject();

        ProcessTokenObject processToken = process.getToken();

        UUID handle = processToken.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(processToken.getClass()), handle);

        content.setResult(handleContext);
    }
}
