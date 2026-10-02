package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.ADefinitionObject;
import indi.sly.subsystem.periphery.proxies.values.CallContextRecord;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.subsystem.periphery.proxies.values.SessionDefinition;
import indi.sly.system.common.supports.UUIDUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionObject extends ADefinitionObject<SessionDefinition> {
    protected ProxyFactory factory;

    public <T extends AServiceProxyObject> T getServiceProxy(Class<T> clazz, CallContextRecord callContext) {
        String taskName = this.factory.acquireTaskName(clazz);

        UUID handle = UUIDUtil.createRandom();

        RemoteDefinition remote = new RemoteDefinition();
        remote.setCallContext(callContext);
        remote.setTask(taskName);
        remote.setValue(UUIDUtil.toString(handle));

        return this.factory.buildProxy(clazz, this.factory.buildRemote(remote));
    }
}
