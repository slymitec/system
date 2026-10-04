package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.ADefinitionObject;
import indi.sly.subsystem.periphery.proxies.values.*;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionObject extends ADefinitionObject<SessionDefinition> {
    protected ProxyFactory factory;

    public <T extends AManagerProxyObject> T getManagerProxy(Class<T> clazz, CallContextProcessRecord callContextProcess) {
        if (ObjectUtil.isAnyNull(clazz, callContextProcess)) {
            throw new ConditionParametersException();
        }

        String taskName = this.factory.acquireTaskName(clazz);
        UUID handle = UUIDUtil.createRandom();
        CallContextRecord callContext = new CallContextRecord(this.definition.getSessionId(), callContextProcess);

        RemoteDefinition remote = new RemoteDefinition();
        remote.setCallContext(callContext);
        remote.setTask(taskName);
        remote.setValue(ObjectUtil.transferToString(handle));

        return this.factory.buildProxy(clazz, this.factory.buildRemote(remote));
    }

    public <T extends AProxyObject> T getProxy(Class<T> clazz, UUID handle, CallContextProcessRecord callContextProcess) {
        if (ObjectUtil.isAnyNull(clazz, callContextProcess)) {
            throw new ConditionParametersException();
        }

        String taskName = this.factory.acquireTaskName(clazz);
        CallContextRecord callContext = new CallContextRecord(this.definition.getSessionId(), callContextProcess);

        RemoteDefinition remote = new RemoteDefinition();
        remote.setCallContext(callContext);
        remote.setTask(taskName);
        remote.setValue(ObjectUtil.transferToString(handle));


        return this.factory.buildProxy(clazz, this.factory.buildRemote(remote));
    }
}
