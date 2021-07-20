package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.GroupEntity;
import indi.sly.system.kernel.security.values.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountObject extends AValueProcessPrototype<AccountEntity> {
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
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.getAccountID().equals(this.getID())
                && !processToken.isPrivilegeType(PrivilegeTypes.SECURITY_DO_WITH_ANY_ACCOUNT)) {
            throw new ConditionRefuseException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setPassword(password);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public Set<GroupObject> getGroups() {
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        this.init();

        Set<GroupObject> groups = new HashSet<>();

        for (GroupEntity group : this.value.getGroups()) {
            groups.add(userManager.getGroup(group.getID()));
        }

        return Collections.unmodifiableSet(groups);
    }

    public void setGroups(Set<GroupObject> groups) {
        if (ObjectUtil.isAnyNull(groups)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeType(PrivilegeTypes.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        UserRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

        if (ObjectUtil.isAnyNull(this.value.getGroups())) {
            this.value.setGroups(new ArrayList<>());
        } else {
            this.value.getGroups().clear();
        }

        for (GroupObject group : groups) {
            this.value.getGroups().add(accountGroupRepository.getGroup(group.getID()));
        }

        this.fresh();
        this.lock(LockType.NONE);
    }

    public UserTokenObject getToken() {
        this.init();

        UserTokenObject accountGroupToken = this.factoryManager.create(UserTokenObject.class);

        accountGroupToken.setSource(() -> this.value.getToken(), (byte[] source) -> this.value.setToken(source));
        accountGroupToken.setLock((lockType) -> {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UserRepositoryObject accountGroupRepository = memoryManager.getAccountGroupRepository();

            accountGroupRepository.lock(this.value, lockType);
        });

        return accountGroupToken;
    }
}
