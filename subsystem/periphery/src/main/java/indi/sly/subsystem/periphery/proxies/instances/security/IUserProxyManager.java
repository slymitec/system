package indi.sly.subsystem.periphery.proxies.instances.security;

import indi.sly.subsystem.periphery.proxies.instances.IProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.security.values.AccountAuthorizationTokenRecord;

import java.util.UUID;

public interface IUserProxyManager extends IProxyManager {
    IAccountProxy getCurrentAccount();

    IAccountProxy getAccountById(UUID accountId);

    IAccountProxy getAccountByName(String accountName);

    IGroupProxy getGroupById(UUID groupId);

    IGroupProxy getGroupByName(String groupName);

    IAccountProxy createAccount(String accountName, String accountPassword);

    IGroupProxy createGroup(String groupName);

    void deleteAccount(UUID accountId);

    void deleteGroup(UUID groupId);

    IAccountAuthorizationProxy authorizeById(UUID accountId);

    IAccountAuthorizationProxy authorizeByName(String accountName, String accountPassword);

    IAccountAuthorizationProxy authorizeByNameWithToken(String accountName, String accountPassword, AccountAuthorizationTokenRecord accountAuthorizationToken);
}
