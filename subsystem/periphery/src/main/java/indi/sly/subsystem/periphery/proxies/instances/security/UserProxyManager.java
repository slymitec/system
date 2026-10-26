package indi.sly.subsystem.periphery.proxies.instances.security;

import indi.sly.subsystem.periphery.proxies.instances.security.prototypes.AccountAuthorizationProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.security.prototypes.AccountProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.security.prototypes.GroupProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.security.values.AccountAuthorizationTokenRecord;
import indi.sly.subsystem.periphery.proxies.prototypes.AManagerProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserProxyManager extends AManagerProxyObject {
    public AccountProxyObject getCurrentAccount() {
        RemoteObject remote = this.remote.invoke("getCurrentAccount");

        return this.factory.buildProxyObject(AccountProxyObject.class, remote);
    }

    public AccountProxyObject getAccountById(UUID accountId) {
        RemoteObject remote = this.remote.invoke("getAccountById", accountId);

        return this.factory.buildProxyObject(AccountProxyObject.class, remote);
    }

    public AccountProxyObject getAccountByName(String accountName) {
        RemoteObject remote = this.remote.invoke("getAccountByName", accountName);

        return this.factory.buildProxyObject(AccountProxyObject.class, remote);
    }

    public GroupProxyObject getGroupById(UUID groupId) {
        RemoteObject remote = this.remote.invoke("getGroupById", groupId);

        return this.factory.buildProxyObject(GroupProxyObject.class, remote);
    }

    public GroupProxyObject getGroupByName(String groupName) {
        RemoteObject remote = this.remote.invoke("getGroupByName", groupName);

        return this.factory.buildProxyObject(GroupProxyObject.class, remote);
    }

    public AccountProxyObject createAccount(String accountName, String accountPassword) {
        RemoteObject remote = this.remote.invoke("createAccount", accountName, accountPassword);

        return this.factory.buildProxyObject(AccountProxyObject.class, remote);
    }

    public GroupProxyObject createGroup(String groupName) {
        RemoteObject remote = this.remote.invoke("createGroup", groupName);

        return this.factory.buildProxyObject(GroupProxyObject.class, remote);
    }

    public void deleteAccount(UUID accountId) {
        this.remote.invoke("deleteAccount", accountId);
    }

    public void deleteGroup(UUID groupId) {
        this.remote.invoke("deleteGroup", groupId);
    }

    public AccountAuthorizationProxyObject authorizeById(UUID accountId) {
        RemoteObject remote = this.remote.invoke("authorizeById", accountId);

        return this.factory.buildProxyObject(AccountAuthorizationProxyObject.class, remote);
    }

    public AccountAuthorizationProxyObject authorizeByName(String accountName, String accountPassword) {
        RemoteObject remote = this.remote.invoke("authorizeByName", accountName, accountPassword);

        return this.factory.buildProxyObject(AccountAuthorizationProxyObject.class, remote);
    }

    public AccountAuthorizationProxyObject authorizeByNameWithToken(String accountName, String accountPassword, AccountAuthorizationTokenRecord accountAuthorizationToken) {
        RemoteObject remote = this.remote.invoke("authorizeByNameWithToken", accountName, accountPassword, accountAuthorizationToken);

        return this.factory.buildProxyObject(AccountAuthorizationProxyObject.class, remote);
    }
}
