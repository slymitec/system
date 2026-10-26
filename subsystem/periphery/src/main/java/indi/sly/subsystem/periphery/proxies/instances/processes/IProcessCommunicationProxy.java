package indi.sly.subsystem.periphery.proxies.instances.processes;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;
import indi.sly.subsystem.periphery.proxies.instances.processes.values.SignalEntryRecord;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IProcessCommunicationProxy extends IProxy {
    byte[] getShared();

    void setShared(byte[] shared);

    Set<UUID> getPortIds();

    UUID createPort(Set<UUID> sourceProcessIds);

    void deleteAllPort();

    void deletePort(UUID portId);

    Set<UUID> getPortSourceProcessIds(UUID portId);

    void setPortSourceProcessIds(UUID portId, Set<UUID> sourceProcessIds);

    byte[] receivePort(UUID portId);

    void sendPort(UUID portId, byte[] value);

    boolean isSignalExist();

    void createSignal(Set<UUID> sourceProcessIds);

    void deleteSignal();

    Set<UUID> getSignalSourceProcessIds();

    void setSignalSourceProcessIds(Set<UUID> sourceProcessIds);

    List<SignalEntryRecord> receiveSignals();

    void sendSignal(UUID signalId, long key, long value);
}