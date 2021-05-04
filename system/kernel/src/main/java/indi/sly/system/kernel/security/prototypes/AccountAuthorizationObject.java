package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.StatusExpiredException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.security.AccountGroupManager;
import indi.sly.system.kernel.security.values.AccountAuthorizationResultDefinition;
import indi.sly.system.kernel.security.values.AccountGroupTokenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountAuthorizationObject extends APrototype {
    public AccountAuthorizationObject() {
        this.date = new HashMap<>();
    }

    private UUID accountID;
    private String password;
    private final Map<Long, Long> date;

    public void setSource(AccountObject account) {
        this.accountID = account.getID();
        this.password = account.getPassword();

        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        this.date.put(DateTimeTypes.CREATE, nowDateTime);
        this.date.put(DateTimeTypes.ACCESS, nowDateTime);
    }

    public Map<Long, Long> getDate() {
        return Collections.unmodifiableMap(this.date);
    }

    public boolean isAccountIDLegal() {
        if (ValueUtil.isAnyNullOrEmpty(this.accountID)) {
            return false;
        }

        AccountGroupManager accountGroupManager = this.factoryManager.getManager(AccountGroupManager.class);

        AccountObject account;
        try {
            account = accountGroupManager.getAccount(this.accountID);
        } catch (AKernelException e) {
            return false;
        }
        if (!ObjectUtil.equals(account.getPassword(), this.password)) {
            return false;
        }

        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();
        long expiredTime =
                this.factoryManager.getKernelSpace().getConfiguration().SECURITY_ACCOUNT_AUTHORIZATION_EXPIRED_TIME;

        if (nowDateTime - this.date.get(DateTimeTypes.CREATE) > expiredTime) {
            return false;
        }

        return true;
    }

    public AccountAuthorizationResultDefinition checkAndGetResult() {
        if (ValueUtil.isAnyNullOrEmpty(this.accountID)) {
            throw new StatusExpiredException();
        }

        AccountGroupManager accountGroupManager = this.factoryManager.getManager(AccountGroupManager.class);

        AccountObject account;
        try {
            account = accountGroupManager.getAccount(this.accountID);
        } catch (AKernelException e) {
            throw new StatusExpiredException();
        }
        if (!ObjectUtil.equals(account.getPassword(), this.password)) {
            throw new StatusExpiredException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();
        long expiredTime =
                this.factoryManager.getKernelSpace().getConfiguration().SECURITY_ACCOUNT_AUTHORIZATION_EXPIRED_TIME;

        if (nowDateTime - this.date.get(DateTimeTypes.CREATE) > expiredTime) {
            throw new StatusExpiredException();
        }

        this.date.put(DateTimeTypes.ACCESS, nowDateTime);

        AccountAuthorizationResultDefinition accountAuthorization = new AccountAuthorizationResultDefinition();
        AccountGroupTokenDefinition accountAuthorizationToken = accountAuthorization.getToken();
        Map<Long, Integer> accountAuthorizationTokenLimits = accountAuthorizationToken.getLimits();

        accountAuthorization.setAccountID(account.getID());

        List<AccountGroupTokenObject> accountGroupTokens = new ArrayList<>();
        List<GroupObject> groups = account.getGroups();
        for (GroupObject group : groups) {
            accountGroupTokens.add(group.getToken());
        }
        accountGroupTokens.add(account.getToken());

        for (AccountGroupTokenObject accountGroupToken : accountGroupTokens) {
            accountAuthorizationToken.setPrivilegeTypes(LogicalUtil.or(accountAuthorizationToken.getPrivilegeTypes()
                    , accountGroupToken.getPrivilegeTypes()));

            for (Map.Entry<Long, Integer> pair : accountGroupToken.getLimits().entrySet()) {
                int value = accountAuthorizationTokenLimits.getOrDefault(pair.getKey(), Integer.MAX_VALUE);
                accountAuthorizationTokenLimits.put(pair.getKey(), Integer.min(value, pair.getValue()));
            }
        }

        return accountAuthorization;
    }
}
