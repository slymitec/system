package indi.sly.system.services.jobs.instances.prototypes.processors.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessCommunicationObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCommunicationObjectTaskInitializer extends ATaskInitializer {
    public ProcessCommunicationObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ProcessManager.class).getFactory().rebuildProcessCommunication(handle);

        this.register("getShared", this::getShared, TransactionType.INDEPENDENCE);
        this.register("setShared", this::setShared, TransactionType.INDEPENDENCE);
        this.register("getPortIds", this::getPortIds, TransactionType.INDEPENDENCE);
        this.register("createPort", this::createPort, TransactionType.INDEPENDENCE);
        this.register("deleteAllPort", this::deleteAllPort, TransactionType.INDEPENDENCE);
        this.register("deletePort", this::deletePort, TransactionType.INDEPENDENCE);
        this.register("getPortSourceProcessIds", this::getPortSourceProcessIds, TransactionType.INDEPENDENCE);
        this.register("setPortSourceProcessIds", this::setPortSourceProcessIds, TransactionType.INDEPENDENCE);
        this.register("receivePort", this::receivePort, TransactionType.INDEPENDENCE);
        this.register("sendPort", this::sendPort, TransactionType.INDEPENDENCE);
        this.register("isSignalExist", this::isSignalExist, TransactionType.INDEPENDENCE);
        this.register("createSignal", this::createSignal, TransactionType.INDEPENDENCE);
        this.register("deleteSignal", this::deleteSignal, TransactionType.INDEPENDENCE);
        this.register("getSignalSourceProcessIds", this::getSignalSourceProcessIds, TransactionType.INDEPENDENCE);
        this.register("setSignalSourceProcessIds", this::setSignalSourceProcessIds, TransactionType.INDEPENDENCE);
        this.register("receiveSignals", this::receiveSignals, TransactionType.INDEPENDENCE);
        this.register("sendSignal", this::sendSignal, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getShared(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        content.setResult(processCommunication.getShared());
    }

    private void setShared(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        byte[] shared = ObjectUtil.transferFromString(byte[].class, parameters.getFirst());

        processCommunication.setShared(shared);
    }

    private void getPortIds(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        content.setResult(processCommunication.getPortIds());
    }

    private void createPort(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Set<UUID> sourceProcessIds = ObjectUtil.transferSetFromString(UUID.class, parameters.getFirst());

        content.setResult(processCommunication.createPort(sourceProcessIds));
    }

    private void deleteAllPort(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        processCommunication.deleteAllPort();
    }

    private void deletePort(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID portId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        processCommunication.deletePort(portId);
    }

    private void getPortSourceProcessIds(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID portId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        content.setResult(processCommunication.getPortSourceProcessIds(portId));
    }

    private void setPortSourceProcessIds(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.size() < 2) {
            throw new ConditionParametersException();
        }

        UUID portId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());
        Set<UUID> sourceProcessIds = ObjectUtil.transferSetFromString(UUID.class, parameters.get(1));

        processCommunication.setPortSourceProcessIds(portId, sourceProcessIds);
    }

    private void receivePort(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID portId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        content.setResult(processCommunication.receivePort(portId));
    }

    private void sendPort(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.size() < 2) {
            throw new ConditionParametersException();
        }

        UUID portId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());
        byte[] value = ObjectUtil.transferFromString(byte[].class, parameters.getFirst());

        processCommunication.sendPort(portId, value);
    }

    private void isSignalExist(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        content.setResult(processCommunication.isSignalExist());
    }

    private void createSignal(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Set<UUID> sourceProcessIds = ObjectUtil.transferSetFromString(UUID.class, parameters.getFirst());

        processCommunication.createSignal(sourceProcessIds);
    }

    private void deleteSignal(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        processCommunication.deleteSignal();
    }

    private void getSignalSourceProcessIds(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        content.setResult(processCommunication.getSignalSourceProcessIds());
    }

    private void setSignalSourceProcessIds(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Set<UUID> sourceProcessIds = ObjectUtil.transferSetFromString(UUID.class, parameters.get(1));

        processCommunication.setSignalSourceProcessIds(sourceProcessIds);
    }

    private void receiveSignals(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        content.setResult(processCommunication.receiveSignals());
    }

    private void sendSignal(TaskContentObject content) {
        ProcessCommunicationObject processCommunication = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.size() < 3) {
            throw new ConditionParametersException();
        }

        UUID signalId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());
        long key = ObjectUtil.transferFromString(Long.class, parameters.getFirst());
        long value = ObjectUtil.transferFromString(Long.class, parameters.getFirst());

        processCommunication.sendSignal(signalId, key, value);
    }
}