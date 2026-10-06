package indi.sly.subsystem.periphery.proxies.instances.processes.prototypes;

import indi.sly.subsystem.periphery.proxies.instances.processes.values.ApplicationRecord;
import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import indi.sly.system.common.values.PathRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessContextProxyObject extends AProxyObject {
    public long getType() {
        RemoteObject remote = this.remote.invoke("getType");

        return this.factory.getValue(Long.class, remote);
    }

    public PathRecord getPath() {
        RemoteObject remote = this.remote.invoke("getPath");

        return this.factory.getValue(PathRecord.class, remote);
    }

    public ApplicationRecord getApplication() {
        RemoteObject remote = this.remote.invoke("getApplication");

        return this.factory.getValue(ApplicationRecord.class, remote);
    }

    public Map<String, String> getEnvironmentVariables() {
        RemoteObject remote = this.remote.invoke("getEnvironmentVariables");

        return this.factory.getMapValue(String.class, String.class, remote);
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariable) {
        this.remote.invoke("setEnvironmentVariables", String.class, environmentVariable);
    }

    public String getParameters() {
        RemoteObject remote = this.remote.invoke("getParameters");

        return this.factory.getValue(String.class, remote);
    }

    public void setParameters(String parameters) {
        this.remote.invoke("setParameters", String.class, parameters);
    }

    public PathRecord getWorkFolder() {
        RemoteObject remote = this.remote.invoke("getWorkFolder");

        return this.factory.getValue(PathRecord.class, remote);
    }

    public void setWorkFolder(PathRecord workFolder) {
        this.remote.invoke("setWorkFolder", PathRecord.class, workFolder);
    }
}
