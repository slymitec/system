package indi.sly.subsystem.periphery.proxies.instances.processes.prototypes;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessStatusProxyObject extends AProxyObject {
    public long get() {
        RemoteObject remote = this.remote.invoke("get");

        return this.factory.getValue(Long.class, remote);
    }

    public void run() {
        this.remote.invoke("run");
    }

    public void interrupt() {
        this.remote.invoke("interrupt");
    }
}
