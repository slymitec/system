package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfiguration;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.AccountAuthorizationCacheEntity;
import indi.sly.system.kernel.security.values.AccountAuthorizationSummaryRecord;
import indi.sly.system.kernel.security.values.AccountAuthorizationTokenRecord;
import indi.sly.system.kernel.security.values.PrivilegeType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountAuthorizationObject extends ACacheableObject<AccountAuthorizationCacheEntity> {
    protected UserFactory factory;

    public Map<Long, Long> getDate() {
        return CollectionUtil.unmodifiable(this.cache.getDate());
    }

    public boolean isLegal() {
        AccountObject account = this.factory.rebuildAccount(this.cache.getAccount());

        if (!ObjectUtil.equals(account.getPassword(), this.cache.getPassword())) {
            return false;
        }

        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();
        long expiredTime =
                this.coreManager.getKernelSpace().getConfiguration().SECURITY_ACCOUNT_AUTHORIZATION_EXPIRED_TIME;

        if (nowDateTime - this.cache.getDate().get(DateTimeType.CREATE) > expiredTime) {
            return false;
        }

        return true;
    }

    public AccountAuthorizationSummaryRecord checkAndGetSummary() {
        KernelConfiguration kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        AccountObject account = this.factory.rebuildAccount(this.cache.getAccount());

        if (!ObjectUtil.equals(account.getPassword(), this.cache.getPassword())) {
            throw new StatusExpiredException();
        }

        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();
        long expiredTime = kernelConfiguration.SECURITY_ACCOUNT_AUTHORIZATION_EXPIRED_TIME;
        if (nowDateTime - this.cache.getDate().get(DateTimeType.CREATE) > expiredTime) {
            throw new StatusExpiredException();
        }

        this.cache.getDate().put(DateTimeType.ACCESS, nowDateTime);

        Set<GroupTokenObject> groupTokens = new HashSet<>();
        Set<GroupObject> groups = account.getGroups();
        for (GroupObject group : groups) {
            groupTokens.add(group.getToken());
        }
        AccountTokenObject accountToken = account.getToken();

        long accountAuthorizationTokenPrivileges = PrivilegeType.NULL;
        Map<Long, Integer> accountAuthorizationTokenLimits = new HashMap<>();
        Set<UUID> accountAuthorizationTokenRoles = new HashSet<>();

        for (GroupTokenObject userToken : groupTokens) {
            accountAuthorizationTokenPrivileges = LogicalUtil.or(accountAuthorizationTokenPrivileges, userToken.getPrivileges());

            for (Map.Entry<Long, Integer> pair : userToken.getLimits().entrySet()) {
                int value = accountAuthorizationTokenLimits.getOrDefault(pair.getKey(), Integer.MAX_VALUE);
                accountAuthorizationTokenLimits.put(pair.getKey(), Integer.min(value, pair.getValue()));
            }
        }
        accountAuthorizationTokenPrivileges = LogicalUtil.or(accountAuthorizationTokenPrivileges, accountToken.getPrivileges());

        for (Map.Entry<Long, Integer> pair : accountToken.getLimits().entrySet()) {
            int value = accountAuthorizationTokenLimits.getOrDefault(pair.getKey(), Integer.MAX_VALUE);
            accountAuthorizationTokenLimits.put(pair.getKey(), Integer.min(value, pair.getValue()));
        }

        if (ObjectUtil.allNotNull(this.cache.getProcessToken(), this.cache.getAccountAuthorizationToken())) {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessTokenObject processToken = processManager.getFactory().rebuildProcessToken(this.cache.getProcessToken());

            if (processToken.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
                accountAuthorizationTokenPrivileges = LogicalUtil.or(accountAuthorizationTokenPrivileges, this.cache.getAccountAuthorizationToken().privileges());
            } else {
                accountAuthorizationTokenPrivileges = LogicalUtil.and(accountAuthorizationTokenPrivileges, this.cache.getAccountAuthorizationToken().privileges());
            }

            int accountAuthorizationTokenLimitValue;
            if (processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
                for (Map.Entry<Long, Integer> pair : this.cache.getAccountAuthorizationToken().limits().entrySet()) {
                    accountAuthorizationTokenLimitValue = accountAuthorizationTokenLimits.getOrDefault(pair.getKey(), pair.getValue());

                    if (accountAuthorizationTokenLimitValue <= pair.getValue()) {
                        accountAuthorizationTokenLimits.put(pair.getKey(), pair.getValue());
                    }
                }
            } else {
                for (Map.Entry<Long, Integer> pair : this.cache.getAccountAuthorizationToken().limits().entrySet()) {
                    accountAuthorizationTokenLimitValue = accountAuthorizationTokenLimits.getOrDefault(pair.getKey(), pair.getValue());

                    if (accountAuthorizationTokenLimitValue > pair.getValue()) {
                        accountAuthorizationTokenLimits.put(pair.getKey(), pair.getValue());
                    }
                }
            }

            accountAuthorizationTokenRoles.addAll(this.cache.getAccountAuthorizationToken().roles());
        }

        AccountAuthorizationTokenRecord accountAuthorizationToken = new AccountAuthorizationTokenRecord(accountAuthorizationTokenPrivileges, accountAuthorizationTokenLimits, accountAuthorizationTokenRoles);

        AccountSessionsObject accountSessions = account.getSessions();
        Set<UUID> accountAuthorizationSessions = new HashSet<>(accountSessions.listSessions());

        AccountAuthorizationSummaryRecord accountAuthorization = new AccountAuthorizationSummaryRecord(account.getId(), account.getName(), account.getPassword(), accountAuthorizationToken, accountAuthorizationSessions);

        return accountAuthorization;
    }
}
