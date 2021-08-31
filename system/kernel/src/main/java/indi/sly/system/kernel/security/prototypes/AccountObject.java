package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.GroupEntity;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountObject extends AIndependentValueProcessObject<AccountEntity> {
    public UUID getID() {
        this.lock(LockType.READ);
        this.init();

        UUID id = this.value.getID();

        this.lock(LockType.NONE);
        return id;
    }

    public String getName() {
        this.lock(LockType.READ);
        this.init();

        String name = this.value.getName();

        this.lock(LockType.NONE);
        return name;
    }

    public String getPassword() {
        this.lock(LockType.READ);
        this.init();

        String password = this.value.getPassword();

        this.lock(LockType.NONE);
        return password;
    }

    public void setPassword(String password) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.getAccountID().equals(this.getID())
                && !processToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setPassword(password);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Set<GroupObject> getGroups() {
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        this.lock(LockType.READ);
        this.init();

        Set<GroupObject> groups = new HashSet<>();

        for (GroupEntity group : this.value.getGroups()) {
            groups.add(userManager.getGroup(group.getID()));
        }

        this.lock(LockType.NONE);
        return CollectionUtil.unmodifiable(groups);
    }

    public void setGroups(Set<GroupObject> groups) {
        if (ObjectUtil.isAnyNull(groups)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionRefuseException();
        }

        UserRepositoryObject accountGroupRepository = memoryManager.getUserRepository();

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (ObjectUtil.isAnyNull(this.value.getGroups())) {
                this.value.setGroups(new ArrayList<>());
            } else {
                this.value.getGroups().clear();
            }

            for (GroupObject group : groups) {
                this.value.getGroups().add(accountGroupRepository.getGroup(group.getID()));
            }

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public UserTokenObject getToken() {
        this.lock(LockType.READ);
        this.init();

        UserTokenObject accountGroupToken = this.factoryManager.create(UserTokenObject.class);

        accountGroupToken.setParent(this);
        accountGroupToken.setSource(() -> this.value.getToken(), (byte[] source) -> this.value.setToken(source));

        this.lock(LockType.NONE);
        return accountGroupToken;
    }
}
