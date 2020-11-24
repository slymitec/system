package indi.sly.system.kernel.processes.communication.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.StatusAlreadyFinishedException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.InfoStatusOpenAttributeTypes;
import indi.sly.system.kernel.processes.communication.prototypes.instances.SignalContentObject;
import indi.sly.system.kernel.processes.communication.prototypes.instances.SignalEntryDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCommunicationObject extends ABytesProcessObject {
    @Override
    protected void read(byte[] source) {
        this.processCommunication = ObjectUtils.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtils.transferToByteArray(this.processCommunication);
    }

    private ProcessCommunicationDefinition processCommunication;

    public UUID getSignalID() {
        this.init();

        return this.processCommunication.getSignalID();
    }

    public void initSignals() {
        this.init();

        if (!UUIDUtils.isAnyNullOrEmpty(this.processCommunication.getSignalID())) {
            throw new StatusAlreadyFinishedException();
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);


        List<Identification> identifications = new ArrayList<>();
        identifications.add(new Identification("Signals"));

        InfoObject signals = objectManager.get(identifications);

        InfoObject signal = signals.createChildAndOpen(UUID.randomUUID(), new Identification(UUIDUtils.createRandom()),
                InfoStatusOpenAttributeTypes.OPEN_ONLYREAD);
        signal.close();
        SecurityDescriptorObject securityDescriptor = signal.getSecurityDescriptor();
        //securityDescriptor

        this.lock(LockTypes.WRITE);
        this.init();

        this.processCommunication.setSignalID(signals.getID());

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public List<SignalEntryDefinition> receiveSignals() {
        this.init();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = this.processCommunication.getSignalID();

        List<Identification> identifications = new ArrayList<>();
        identifications.add(new Identification("Signals"));
        identifications.add(new Identification(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoStatusOpenAttributeTypes.OPEN_SHARED_WRITE);

        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        List<SignalEntryDefinition> signalEntries = signalContent.receive();

        signal.close();

        return signalEntries;
    }

    public void sendSignal(ProcessObject targetProcess, long key, long value) {
        if (ObjectUtils.isAnyNull(targetProcess)) {
            throw new ConditionParametersException();
        }

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        UUID signalID = targetProcess.getCommunication().getSignalID();

        List<Identification> identifications = new ArrayList<>();
        identifications.add(new Identification("Signals"));
        identifications.add(new Identification(signalID));

        InfoObject signal = objectManager.get(identifications);
        signal.open(InfoStatusOpenAttributeTypes.OPEN_SHARED_WRITE);

        SignalContentObject signalContent = (SignalContentObject) signal.getContent();
        signalContent.send(key, value);

        signal.close();
    }

}
