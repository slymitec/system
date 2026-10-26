package indi.sly.subsystem.periphery.proxies.instances.services.instances;

import indi.sly.subsystem.periphery.proxies.instances.objects.IInfoContentProxy;
import indi.sly.system.common.values.PathRecord;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IServiceContentProxy extends IInfoContentProxy {
    List<UUID> getDependencies();

    String getSecret();

    void setSecret(String secret);

    PathRecord getPath();

    void setPath(PathRecord path);

    UUID getAccountId();

    void setAccountId(UUID accountId);

    long getMode();

    void setMode(long mode);

    long getStart();

    void setStart(long start);

    Map<String, String> getEnvironmentVariables();

    void setEnvironmentVariables(Map<String, String> environmentVariables);

    String getParameters();

    void setParameters(String parameters);
}
