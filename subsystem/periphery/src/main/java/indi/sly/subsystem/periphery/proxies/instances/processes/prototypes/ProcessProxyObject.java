package indi.sly.subsystem.periphery.proxies.instances.processes.prototypes;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessProxyObject extends AProxyObject {

    public UUID getId() {
        RemoteObject remote = this.remote.invoke("getId");

        return this.factory.getValue(UUID.class, remote);
    }

    public UUID getParentId() {
        RemoteObject remote = this.remote.invoke("getParentId");

        return this.factory.getValue(UUID.class, remote);
    }

    public boolean isCurrent() {
        RemoteObject remote = this.remote.invoke("isCurrent");

        return this.factory.getValue(Boolean.class, remote);
    }

    public ProcessStatusProxyObject getStatus() {
        RemoteObject remote = this.remote.invoke("getStatus");

        return this.factory.getValue(ProcessStatusProxyObject.class, remote);
    }

    public ProcessCommunicationProxyObject getCommunication() {
        RemoteObject remote = this.remote.invoke("getCommunication");

        return this.factory.getValue(ProcessCommunicationProxyObject.class, remote);
    }

    public ProcessContextProxyObject getContext() {
        RemoteObject remote = this.remote.invoke("getContext");

        return this.factory.getValue(ProcessContextProxyObject.class, remote);
    }

    public ProcessInfoTableProxyObject getInfoTable() {
        RemoteObject remote = this.remote.invoke("getInfoTable");

        return this.factory.getValue(ProcessInfoTableProxyObject.class, remote);
    }

    public ProcessSessionProxyObject getSession() {
        RemoteObject remote = this.remote.invoke("getSession");

        return this.factory.getValue(ProcessSessionProxyObject.class, remote);
    }

    public ProcessStatisticsProxyObject getStatistics() {
        RemoteObject remote = this.remote.invoke("getStatistics");

        return this.factory.getValue(ProcessStatisticsProxyObject.class, remote);
    }

    public ProcessTokenProxyObject getToken() {
        RemoteObject remote = this.remote.invoke("getToken");

        return this.factory.getValue(ProcessTokenProxyObject.class, remote);
    }
}
