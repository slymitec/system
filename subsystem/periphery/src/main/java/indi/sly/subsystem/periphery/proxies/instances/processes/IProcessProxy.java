package indi.sly.subsystem.periphery.proxies.instances.processes;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;

import java.util.UUID;

public interface IProcessProxy extends IProxy {
    UUID getId();

    UUID getParentId();

    boolean isCurrent();

    IProcessStatusProxy getStatus();

    IProcessCommunicationProxy getCommunication();

    IProcessContextProxy getContext();

    IProcessInfoTableProxy getInfoTable();

    IProcessSessionProxy getSession();

    IProcessStatisticsProxy getStatistics();

    IProcessTokenProxy getToken();
}