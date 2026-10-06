package indi.sly.subsystem.periphery.proxies.instances.security.prototypes;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountProxyObject extends AProxyObject {
    public UUID getId() {
        RemoteObject remote = this.remote.invoke("getId");

        return this.factory.getValue(UUID.class, remote);
    }

    public String getName() {
        RemoteObject remote = this.remote.invoke("getName");

        return this.factory.getValue(String.class, remote);
    }

    public String getPassword() {
        RemoteObject remote = this.remote.invoke("getPassword");

        return this.factory.getValue(String.class, remote);
    }

    public void setPassword(String password) {
        RemoteObject remote = this.remote.invoke("setPassword", password);
    }

    public Set<GroupProxyObject> getGroups() {
        RemoteObject remote = this.remote.invoke("getGroups");

        return this.factory.buildProxySet(GroupProxyObject.class, remote);
    }

    public void setGroups(Set<GroupProxyObject> groups) {
        Set<UUID> handles = new HashSet<>();

        if (ObjectUtil.allNotNull(groups)) {
            for (GroupProxyObject group : groups) {
                handles.add(group.getHandle());
            }
        }

        this.remote.invoke("setGroups", handles);
    }

    public GroupTokenProxyObject getToken() {
        RemoteObject remote = this.remote.invoke("getToken");

        return this.factory.buildProxy(GroupTokenProxyObject.class, remote);
    }

    public AccountSessionsProxyObject getSessions() {
        RemoteObject remote = this.remote.invoke("getSessions");

        return this.factory.buildProxy(AccountSessionsProxyObject.class, remote);
    }
}
