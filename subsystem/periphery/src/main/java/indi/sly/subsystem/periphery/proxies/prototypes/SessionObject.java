package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.ADefinitionObject;
import indi.sly.subsystem.periphery.proxies.values.*;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionObject extends ADefinitionObject<SessionDefinition> {
    protected ProxyFactory factory;

    public <T extends AManagerProxyObject> T getManagerProxy(Class<T> clazz, CallContextProcessRecord callContextProcess) {
        return this.getProxy(clazz, UUIDUtil.createRandom(), callContextProcess);
    }

    public <T extends AProxyObject> T getProxy(Class<T> clazz, UUID handle, CallContextProcessRecord callContextProcess) {
        if (ObjectUtil.isAnyNull(clazz, callContextProcess) || ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        CallContextRecord callContext = new CallContextRecord(this.definition.getSessionId(), callContextProcess);

        RemoteDefinition remote = new RemoteDefinition();
        remote.setCallContext(callContext);
        remote.setValue(ObjectUtil.transferToString(handle));

        return this.factory.buildProxy(clazz, this.factory.buildRemote(remote));
    }

    private String doGetProxyAndInvoke(String taskName, UUID handle, CallContextProcessRecord callContextProcess, ClientRequestRecord clientRequest) {
        if (ValueUtil.isAnyNullOrEmpty(handle) || ObjectUtil.isAnyNull(callContextProcess, clientRequest)) {
            throw new ConditionParametersException();
        }

        CallContextRecord callContext = new CallContextRecord(this.definition.getSessionId(), callContextProcess);

        RemoteDefinition remote = new RemoteDefinition();
        remote.setCallContext(callContext);
        remote.setTaskName(taskName);
        remote.setValue(ObjectUtil.transferToString(handle));
        RemoteObject requestRemote = this.factory.buildRemote(remote);

        RemoteObject responseRemote = requestRemote.invoke(clientRequest.method(), clientRequest.parameters().toArray());

        return responseRemote.getValue();
    }

    public String getProxyAndInvoke(String taskName, UUID handle, CallContextProcessRecord callContextProcess, ClientRequestRecord clientRequest) {
        if (ValueUtil.isAnyNullOrEmpty(taskName)) {
            throw new ConditionParametersException();
        }

        if (!this.factory.isTaskExist(taskName)) {
            throw new StatusNotExistedException();
        }

        return this.doGetProxyAndInvoke(taskName, handle, callContextProcess, clientRequest);
    }


    public String getProxyAndInvoke(Class<? extends AProxyObject> clazz, UUID handle, CallContextProcessRecord callContextProcess, ClientRequestRecord clientRequest) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        String taskName = this.factory.acquireTaskName(clazz);
        if (ValueUtil.isAnyNullOrEmpty(taskName)) {
            throw new StatusNotExistedException();
        }

        return this.doGetProxyAndInvoke(taskName, handle, callContextProcess, clientRequest);
    }
}