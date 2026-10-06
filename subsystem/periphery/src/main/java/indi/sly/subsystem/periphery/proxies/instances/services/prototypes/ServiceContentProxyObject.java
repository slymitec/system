package indi.sly.subsystem.periphery.proxies.instances.services.prototypes;

import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.AInfoContentProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.PathRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceContentProxyObject extends AInfoContentProxyObject {
    public List<UUID> getDependencies() {
        RemoteObject remote = this.remote.invoke("getDependencies");

        return this.factory.getListValue(UUID.class, remote);
    }

    public String getSecret() {
        RemoteObject remote = this.remote.invoke("getSecret");

        return this.factory.getValue(String.class, remote);
    }

    public void setSecret(String secret) {
        this.remote.invoke("setSecret", secret);
    }

    public PathRecord getPath() {
        RemoteObject remote = this.remote.invoke("getPath");

        return this.factory.getValue(PathRecord.class, remote);
    }

    public void setPath(PathRecord path) {
        this.remote.invoke("setPath", path);
    }

    public UUID getAccountId() {
        RemoteObject remote = this.remote.invoke("getAccountId");

        return this.factory.getValue(UUID.class, remote);
    }

    public void setAccountId(UUID accountId) {
        this.remote.invoke("setAccountId", accountId);
    }

    public long getMode() {
        RemoteObject remote = this.remote.invoke("getMode");

        return this.factory.getValue(Long.class, remote);
    }

    public void setMode(long mode) {
        this.remote.invoke("setMode", mode);
    }

    public long getStart() {
        RemoteObject remote = this.remote.invoke("getStart");

        return this.factory.getValue(Long.class, remote);
    }

    public void setStart(long start) {
        this.remote.invoke("setStart", start);
    }

    public Map<String, String> getEnvironmentVariables() {
        RemoteObject remote = this.remote.invoke("getEnvironmentVariables");

        return this.factory.getMapValue(String.class, String.class, remote);
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        this.remote.invoke("setEnvironmentVariables", environmentVariables);
    }

    public String getParameters() {
        RemoteObject remote = this.remote.invoke("getParameters");

        return this.factory.getValue(String.class, remote);
    }

    public void setParameters(String parameters) {
        this.remote.invoke("setParameters", parameters);
    }
}
