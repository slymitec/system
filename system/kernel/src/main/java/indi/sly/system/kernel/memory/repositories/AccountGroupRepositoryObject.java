package indi.sly.system.kernel.memory.repositories;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.StatusAlreadyExistedException;
import indi.sly.system.common.exceptions.StatusNotExistedException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.ACoreObject;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
import indi.sly.system.kernel.security.entities.AccountEntity;
import indi.sly.system.kernel.security.entities.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountGroupRepositoryObject extends ACoreObject {
    @PersistenceContext
    private EntityManager entityManager;

    public boolean containAccount(UUID id) {
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        AccountEntity account = this.entityManager.find(AccountEntity.class, id);

        return ObjectUtils.isAnyNull(account);
    }

    public boolean containGroup(UUID id) {
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        GroupEntity group = this.entityManager.find(GroupEntity.class, id);

        return ObjectUtils.isAnyNull(group);
    }

    public AccountEntity getAccount(UUID id) {
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        AccountEntity group = this.entityManager.find(AccountEntity.class, id);

        if (ObjectUtils.isAnyNull(group)) {
            throw new StatusNotExistedException();
        }

        return group;
    }

    public GroupEntity getGroup(UUID id) {
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        GroupEntity account = this.entityManager.find(GroupEntity.class, id);

        if (ObjectUtils.isAnyNull(account)) {
            throw new StatusNotExistedException();
        }

        return account;
    }

    public void add(AccountEntity account) {
        if (ObjectUtils.isAnyNull(account)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(account)) {
            throw new StatusAlreadyExistedException();
        }

        this.entityManager.merge(account);
    }

    public void add(GroupEntity group) {
        if (ObjectUtils.isAnyNull(group)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(group)) {
            throw new StatusAlreadyExistedException();
        }

        this.entityManager.merge(group);
    }

    public void delete(AccountEntity account) {
        if (ObjectUtils.isAnyNull(account)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(account)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(account);
    }

    public void delete(GroupEntity group) {
        if (ObjectUtils.isAnyNull(group)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(group)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(group);
    }

    public void lock(AccountEntity account, long lockType) {
        if (ObjectUtils.isAnyNull(account)) {
            throw new ConditionParametersException();
        }

        LockModeType lockModeType;
        if (lockType == LockTypes.READ) {
            lockModeType = LockModeType.PESSIMISTIC_READ;
        } else if (lockType == LockTypes.WRITE) {
            lockModeType = LockModeType.PESSIMISTIC_WRITE;
        } else {
            lockModeType = LockModeType.NONE;
        }

        this.entityManager.lock(account, lockModeType);
    }

    public void lock(GroupEntity group, long lockType) {
        if (ObjectUtils.isAnyNull(group)) {
            throw new ConditionParametersException();
        }

        LockModeType lockModeType;
        if (lockType == LockTypes.READ) {
            lockModeType = LockModeType.PESSIMISTIC_READ;
        } else if (lockType == LockTypes.WRITE) {
            lockModeType = LockModeType.PESSIMISTIC_WRITE;
        } else {
            lockModeType = LockModeType.NONE;
        }

        this.entityManager.lock(group, lockModeType);
    }
}
