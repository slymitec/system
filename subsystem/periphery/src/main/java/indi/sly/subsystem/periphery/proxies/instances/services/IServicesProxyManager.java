package indi.sly.subsystem.periphery.proxies.instances.services;

import indi.sly.subsystem.periphery.proxies.instances.IProxyManager;
import indi.sly.system.common.values.PathRecord;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IServicesProxyManager extends IProxyManager {
    void createService(UUID serviceId, List<UUID> dependencies, String secret, PathRecord path, UUID accountId,
                       long mode, long start, Map<String, String> environmentVariables, String parameters);

    void deleteService(UUID serviceId);

    void start(UUID serviceId);

    void stop(UUID serviceId);
}
