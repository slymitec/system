package indi.sly.subsystem.periphery.proxies.instances.processes.prototypes;

import indi.sly.subsystem.periphery.proxies.instances.processes.values.SignalEntryRecord;
import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCommunicationProxyObject extends AProxyObject {
    public byte[] getShared() {
        RemoteObject remote = this.remote.invoke("getShared");

        return this.factory.getValue(byte[].class, remote);
    }

    public void setShared(byte[] shared) {
        this.remote.invoke("setShared", (Object) shared);
    }

    public Set<UUID> getPortIds() {
        RemoteObject remote = this.remote.invoke("getPortIds");

        return this.factory.getSetValue(UUID.class, remote);
    }

    public UUID createPort(Set<UUID> sourceProcessIds) {
        RemoteObject remote = this.remote.invoke("createPort", sourceProcessIds);

        return this.factory.getValue(UUID.class, remote);
    }

    public void deleteAllPort() {
        this.remote.invoke("deleteAllPort");
    }

    public void deletePort(UUID portId) {
        this.remote.invoke("deletePort", portId);
    }

    public Set<UUID> getPortSourceProcessIds(UUID portId) {
        RemoteObject remote = this.remote.invoke("getPortSourceProcessIds", portId);

        return this.factory.getSetValue(UUID.class, remote);
    }

    public void setPortSourceProcessIds(UUID portId, Set<UUID> sourceProcessIds) {
        this.remote.invoke("setPortSourceProcessIds", portId, sourceProcessIds);
    }

    public byte[] receivePort(UUID portId) {
        RemoteObject remote = this.remote.invoke("receivePort", portId);

        return this.factory.getValue(byte[].class, remote);
    }

    public void sendPort(UUID portId, byte[] value) {
        this.remote.invoke("sendPort", portId, value);
    }

    public boolean isSignalExist() {
        RemoteObject remote = this.remote.invoke("isSignalExist");

        return this.factory.getValue(Boolean.class, remote);
    }

    public void createSignal(Set<UUID> sourceProcessIds) {
        this.remote.invoke("createSignal", sourceProcessIds);
    }

    public void deleteSignal() {
        this.remote.invoke("deleteSignal");
    }

    public Set<UUID> getSignalSourceProcessIds() {
        RemoteObject remote = this.remote.invoke("getSignalSourceProcessIds");

        return this.factory.getSetValue(UUID.class, remote);
    }

    public void setSignalSourceProcessIds(Set<UUID> sourceProcessIds) {
        this.remote.invoke("setSignalSourceProcessIds", sourceProcessIds);
    }

    public List<SignalEntryRecord> receiveSignals() {
        RemoteObject remote = this.remote.invoke("receiveSignals");

        return this.factory.getListValue(SignalEntryRecord.class, remote);
    }

    public void sendSignal(UUID signalId, long key, long value) {
        this.remote.invoke("sendSignal", signalId, key, value);
    }
}
