package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusExpiredException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.lang.AccountAuthorizationGetAccount;
import indi.sly.system.kernel.security.values.AccountAuthorizationSummaryDefinition;
import indi.sly.system.kernel.security.values.AccountAuthorizationTokenDefinition;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountAuthorizationObject extends AObject {
    public AccountAuthorizationObject() {
        this.date = new HashMap<>();
    }

    private AccountAuthorizationGetAccount account;
    private String password;
    private final Map<Long, Long> date;
    private ProcessTokenObject processToken;
    private AccountAuthorizationTokenDefinition accountAuthorizationToken;

    public void setAccount(AccountAuthorizationGetAccount account, String password) {
        if (ObjectUtil.isAnyNull(account)) {
            throw new ConditionParametersException();
        }

        this.account = account;
        this.password = password;

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        this.date.put(DateTimeType.CREATE, nowDateTime);
        this.date.put(DateTimeType.ACCESS, nowDateTime);
    }

    public void setToken(ProcessTokenObject processToken, AccountAuthorizationTokenDefinition accountAuthorizationToken) {
        this.processToken = processToken;
        this.accountAuthorizationToken = accountAuthorizationToken;
    }

    public Map<Long, Long> getDate() {
        return CollectionUtil.unmodifiable(this.date);
    }

    public boolean isLegal() {
        if (ObjectUtil.isAnyNull(this.account)) {
            return false;
        }

        AccountObject account;
        try {
            account = this.account.acquire();
        } catch (AKernelException e) {
            return false;
        }
        if (!ObjectUtil.equals(account.getPassword(), this.password)) {
            return false;
        }

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();
        long expiredTime =
                this.factoryManager.getKernelSpace().getConfiguration().SECURITY_ACCOUNT_AUTHORIZATION_EXPIRED_TIME;

        if (nowDateTime - this.date.get(DateTimeType.CREATE) > expiredTime) {
            return false;
        }

        return true;
    }

    public AccountAuthorizationSummaryDefinition checkAndGetSummary() {
        if (ObjectUtil.isAnyNull(this.account)) {
            throw new ConditionContextException();
        }

        AccountObject account;
        try {
            account = this.account.acquire();
        } catch (AKernelException e) {
            throw new StatusExpiredException();
        }
        if (!ObjectUtil.equals(account.getPassword(), this.password)) {
            throw new StatusExpiredException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();
        long expiredTime =
                this.factoryManager.getKernelSpace().getConfiguration().SECURITY_ACCOUNT_AUTHORIZATION_EXPIRED_TIME;

        if (nowDateTime - this.date.get(DateTimeType.CREATE) > expiredTime) {
            throw new StatusExpiredException();
        }

        this.date.put(DateTimeType.ACCESS, nowDateTime);

        Set<UserTokenObject> userTokens = new HashSet<>();
        Set<GroupObject> groups = account.getGroups();
        for (GroupObject group : groups) {
            userTokens.add(group.getToken());
        }
        userTokens.add(account.getToken());

        AccountAuthorizationSummaryDefinition accountAuthorization = new AccountAuthorizationSummaryDefinition();
        accountAuthorization.setID(account.getID());
        accountAuthorization.setName(account.getName());
        accountAuthorization.setPassword(account.getPassword());
        AccountAuthorizationTokenDefinition accountAuthorizationToken = accountAuthorization.getToken();
        Map<Long, Integer> accountAuthorizationTokenLimits = accountAuthorizationToken.getLimits();
        for (UserTokenObject userToken : userTokens) {
            accountAuthorizationToken.setPrivileges(LogicalUtil.or(accountAuthorizationToken.getPrivileges(),
                    userToken.getPrivileges()));

            for (Map.Entry<Long, Integer> pair : userToken.getLimits().entrySet()) {
                int value = accountAuthorizationTokenLimits.getOrDefault(pair.getKey(), Integer.MAX_VALUE);
                accountAuthorizationTokenLimits.put(pair.getKey(), Integer.min(value, pair.getValue()));
            }
        }

        if (ObjectUtil.allNotNull(this.processToken, this.accountAuthorizationToken)) {
            if (this.processToken.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
                accountAuthorizationToken.setPrivileges(LogicalUtil.or(accountAuthorizationToken.getPrivileges(),
                        this.accountAuthorizationToken.getPrivileges()));
            } else {
                accountAuthorizationToken.setPrivileges(LogicalUtil.and(accountAuthorizationToken.getPrivileges(),
                        this.accountAuthorizationToken.getPrivileges()));
            }

            int accountAuthorizationTokenLimitValue;
            if (this.processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
                for (Map.Entry<Long, Integer> pair : this.accountAuthorizationToken.getLimits().entrySet()) {
                    accountAuthorizationTokenLimitValue = accountAuthorizationTokenLimits.getOrDefault(pair.getKey(), pair.getValue());

                    if (accountAuthorizationTokenLimitValue <= pair.getValue()) {
                        accountAuthorizationTokenLimits.put(pair.getKey(), pair.getValue());
                    }
                }
            } else {
                for (Map.Entry<Long, Integer> pair : this.accountAuthorizationToken.getLimits().entrySet()) {
                    accountAuthorizationTokenLimitValue = accountAuthorizationTokenLimits.getOrDefault(pair.getKey(), pair.getValue());

                    if (accountAuthorizationTokenLimitValue > pair.getValue()) {
                        accountAuthorizationTokenLimits.put(pair.getKey(), pair.getValue());
                    }
                }
            }

            accountAuthorizationToken.getRoles().addAll(this.accountAuthorizationToken.getRoles());
        }
        UserSessionObject accountSession = account.getSession();
        accountAuthorization.getSessionNames().addAll(accountSession.getAllNames());

        return accountAuthorization;
    }
}
