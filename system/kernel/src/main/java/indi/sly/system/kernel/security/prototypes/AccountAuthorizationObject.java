package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.exceptions.AKernelException;
import indi.sly.system.common.exceptions.StatusExpiredException;
import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.security.SecurityTokenManager;
import indi.sly.system.kernel.security.definitions.AccountAuthorizationResultDefinition;
import indi.sly.system.kernel.security.definitions.AccountGroupTokenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountAuthorizationObject extends ACoreObject {
    public AccountAuthorizationObject() {
        this.date = new HashMap<>();
    }

    private UUID accountID;
    private String password;
    private final Map<Long, Long> date;

    public void setSource(AccountObject account) {
        this.accountID = account.getID();
        this.password = account.getPassword();

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        this.date.put(DateTimeTypes.CREATE, nowDateTime);
        this.date.put(DateTimeTypes.ACCESS, nowDateTime);
    }

    public Map<Long, Long> getDate() {
        return Collections.unmodifiableMap(this.date);
    }

    public boolean isAccountIDLegal() {
        if (UUIDUtils.isAnyNullOrEmpty(this.accountID)) {
            return false;
        }

        SecurityTokenManager securityTokenManager = this.factoryManager.getManager(SecurityTokenManager.class);

        AccountObject account;
        try {
            account = securityTokenManager.getAccount(this.accountID);
        } catch (AKernelException e) {
            return false;
        }
        if (!StringUtils.equals(account.getPassword(), this.password)) {
            return false;
        }

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL,
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
        if (UUIDUtils.isAnyNullOrEmpty(this.accountID)) {
            throw new StatusExpiredException();
        }

        SecurityTokenManager securityTokenManager = this.factoryManager.getManager(SecurityTokenManager.class);

        AccountObject account;
        try {
            account = securityTokenManager.getAccount(this.accountID);
        } catch (AKernelException e) {
            throw new StatusExpiredException();
        }
        if (!StringUtils.equals(account.getPassword(), this.password)) {
            throw new StatusExpiredException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL,
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
            accountAuthorizationToken.setPrivilegeTypes(LogicalUtils.or(accountAuthorizationToken.getPrivilegeTypes()
                    , accountGroupToken.getPrivilegeTypes()));

            for (Map.Entry<Long, Integer> pair : accountGroupToken.getLimits().entrySet()) {
                int value = accountAuthorizationTokenLimits.getOrDefault(pair.getKey(), Integer.MAX_VALUE);
                accountAuthorizationTokenLimits.put(pair.getKey(), Integer.min(value, pair.getValue()));
            }
        }

        return accountAuthorization;
    }
}
