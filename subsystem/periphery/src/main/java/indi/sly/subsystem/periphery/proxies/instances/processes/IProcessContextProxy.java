package indi.sly.subsystem.periphery.proxies.instances.processes;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;
import indi.sly.subsystem.periphery.proxies.instances.processes.values.ApplicationRecord;
import indi.sly.system.common.values.PathRecord;

import java.util.Map;

public interface IProcessContextProxy extends IProxy {
    long getType();

    PathRecord getPath();

    ApplicationRecord getApplication();

    Map<String, String> getEnvironmentVariables();

    void setEnvironmentVariables(Map<String, String> environmentVariable);

    String getParameters();

    void setParameters(String parameters);

    PathRecord getWorkFolder();

    void setWorkFolder(PathRecord workFolder);
}