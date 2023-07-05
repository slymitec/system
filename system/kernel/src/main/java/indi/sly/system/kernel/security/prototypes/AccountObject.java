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

import jakarta.inject.Named;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountObject extends AIndependentValueProcessObject<AccountEntity> {
    public UUID getID() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getID();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public String getName() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getName();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public String getPassword() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getPassword();
        } finally {
            this.lock(LockType.NONE);
        }
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

        try {
            this.lock(LockType.READ);
            this.init();

            Set<GroupObject> groups = new HashSet<>();

            for (GroupEntity group : this.value.getGroups()) {
                groups.add(userManager.getGroup(group.getID()));
            }

            return CollectionUtil.unmodifiable(groups);
        } finally {
            this.lock(LockType.NONE);
        }
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

        UserRepositoryObject userRepository = memoryManager.getUserRepository();

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (ObjectUtil.isAnyNull(this.value.getGroups())) {
                this.value.setGroups(new ArrayList<>());
            } else {
                this.value.getGroups().clear();
            }

            for (GroupObject group : groups) {
                this.value.getGroups().add(userRepository.getGroup(group.getID()));
            }

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public UserTokenObject getToken() {
        try {
            this.lock(LockType.READ);
            this.init();

            UserTokenObject userToken = this.factoryManager.create(UserTokenObject.class);

            userToken.setParent(this);
            userToken.setSource(() -> this.value.getToken(), (byte[] source) -> this.value.setToken(source));

            return userToken;
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public AccountSessionsObject getSessions() {
        try {
            this.lock(LockType.READ);
            this.init();

            AccountSessionsObject accountSessions = this.factoryManager.create(AccountSessionsObject.class);

            accountSessions.setParent(this);
            accountSessions.setSource(() -> this.value.getSessions(), (byte[] source) -> this.value.setSessions(source));

            return accountSessions;
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
