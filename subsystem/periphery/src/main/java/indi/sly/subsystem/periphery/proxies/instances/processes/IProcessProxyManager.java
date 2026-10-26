package indi.sly.subsystem.periphery.proxies.instances.processes;

import indi.sly.subsystem.periphery.proxies.instances.IProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.processes.values.ProcessAdditionalCreatorRecord;
import indi.sly.subsystem.periphery.proxies.instances.security.IAccountAuthorizationProxy;
import indi.sly.system.common.values.PathRecord;

import java.util.UUID;

public interface IProcessProxyManager extends IProxyManager {
    IProcessProxy getCurrent();

    IProcessProxy getWithAuthorization(UUID processId, IAccountAuthorizationProxy accountAuthorization);

    IProcessProxy get(UUID processId);

    IProcessProxy create(IAccountAuthorizationProxy accountAuthorization, UUID fileIndex, String parameters,
                         PathRecord workFolder, ProcessAdditionalCreatorRecord additionalCreator);

    void endCurrent();

    void end(UUID processId);
}