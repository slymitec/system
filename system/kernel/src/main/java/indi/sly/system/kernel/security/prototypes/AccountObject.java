package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AccountGroupRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.SecurityTokenManager;
import indi.sly.system.kernel.security.entities.AccountEntity;
import indi.sly.system.kernel.security.entities.GroupEntity;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountObject extends AValueProcessObject<AccountEntity> {
    public UUID getID() {
        this.init();

        return this.value.getID();
    }

    public String getName() {
        this.init();

        return this.value.getName();
    }

    public String getPassword() {
        return this.value.getPassword();
    }

    public void setPassword(String password) {
        this.lock(LockTypes.WRITE);
        this.init();

        this.value.setPassword(password);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public List<GroupObject> getGroups() {
        SecurityTokenManager securityTokenManager = this.factoryManager.getManager(SecurityTokenManager.class);

        this.init();

        List<GroupObject> groups = new ArrayList<>();

        for (GroupEntity group : this.value.getGroups()) {
            groups.add(securityTokenManager.getGroup(group.getID()));
        }

        return Collections.unmodifiableList(groups);
    }

    public void setGroups(List<GroupObject> groups) {
        if (ObjectUtils.isAnyNull(groups)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

        if (ObjectUtils.isAnyNull(this.value.getGroups())) {
            this.value.setGroups(new ArrayList<>());
        } else {
            this.value.getGroups().clear();
        }

        for (GroupObject group : groups) {
            this.value.getGroups().add(accountGroupRepository.getGroup(group.getID()));
        }

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public AccountGroupTokenObject getToken() {
        this.init();

        AccountGroupTokenObject accountGroupToken = this.factoryManager.create(AccountGroupTokenObject.class);

        accountGroupToken.setSource(() -> this.value.getToken(), (byte[] source) -> {
            this.value.setToken(source);
        });
        accountGroupToken.setLock((lockType) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            AccountGroupRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

            accountGroupRepository.lock(this.value, lockType);
        });

        return accountGroupToken;
    }
}
