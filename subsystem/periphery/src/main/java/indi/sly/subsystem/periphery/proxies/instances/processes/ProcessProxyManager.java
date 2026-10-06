package indi.sly.subsystem.periphery.proxies.instances.processes;

import indi.sly.subsystem.periphery.proxies.instances.processes.prototypes.ProcessProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.processes.values.ProcessAdditionalCreatorRecord;
import indi.sly.subsystem.periphery.proxies.instances.security.prototypes.AccountAuthorizationProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.AManagerProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import indi.sly.system.common.values.PathRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessProxyManager extends AManagerProxyObject {
    public ProcessProxyObject getCurrent() {
        RemoteObject remote = this.remote.invoke("getCurrent");

        return this.factory.buildProxy(ProcessProxyObject.class, remote);
    }

    public ProcessProxyObject getWithAuthorization(UUID processId, AccountAuthorizationProxyObject accountAuthorization) {
        RemoteObject remote = this.remote.invoke("getWithAuthorization", processId, accountAuthorization.getHandle());

        return this.factory.buildProxy(ProcessProxyObject.class, remote);
    }

    public ProcessProxyObject get(UUID processId) {
        RemoteObject remote = this.remote.invoke("get");

        return this.factory.buildProxy(ProcessProxyObject.class, remote);
    }

    public ProcessProxyObject create(AccountAuthorizationProxyObject accountAuthorization, UUID fileIndex, String parameters, PathRecord workFolder, ProcessAdditionalCreatorRecord additionalCreator) {
        RemoteObject remote = this.remote.invoke("create", accountAuthorization.getHandle(), fileIndex, parameters, workFolder, additionalCreator);

        return this.factory.buildProxy(ProcessProxyObject.class, remote);
    }

    public void endCurrent() {
        this.remote.invoke("endCurrent");
    }

    public void end(UUID processId) {
        this.remote.invoke("end", processId);
    }
}
