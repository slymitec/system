package indi.sly.subsystem.periphery.proxies.instances.services;

import indi.sly.subsystem.periphery.proxies.prototypes.AManagerProxyObject;
import indi.sly.system.common.values.PathRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServicesProxyManager extends AManagerProxyObject {
    public void createService(UUID serviceId, List<UUID> dependencies, String secret, PathRecord path, UUID accountId,
                              long mode, long start, Map<String, String> environmentVariables, String parameters) {
        this.remote.invoke("createService", serviceId, dependencies, secret, path, accountId, mode, start, environmentVariables, parameters);
    }

    public void deleteService(UUID serviceId) {
        this.remote.invoke("deleteService", serviceId);
    }


    public void start(UUID serviceId) {
        this.remote.invoke("startService", serviceId);
    }

    public void stop(UUID serviceId) {
        this.remote.invoke("stopService", serviceId);
    }
}
