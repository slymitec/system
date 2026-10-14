package indi.sly.system.kernel.memory.repositories.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.security.values.AccountEntity;
import indi.sly.system.kernel.security.values.GroupEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserRepositoryObject extends AObject {
    @PersistenceContext
    private EntityManager entityManager;

    public boolean containAccount(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        AccountEntity account = this.entityManager.find(AccountEntity.class, id);

        return ObjectUtil.notNull(account);
    }

    public boolean containGroup(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        GroupEntity group = this.entityManager.find(GroupEntity.class, id);

        return ObjectUtil.notNull(group);
    }

    public AccountEntity getAccount(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        AccountEntity account = this.entityManager.find(AccountEntity.class, id);

        if (ObjectUtil.isNull(account)) {
            throw new StatusNotExistedException();
        }

        return account;
    }

    public AccountEntity getAccount(UUID id, long lock) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode;
        if (LogicalUtil.isAnyEqual(lock, LockType.READ)) {
            lockMode = LockModeType.PESSIMISTIC_READ;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.WRITE)) {
            lockMode = LockModeType.PESSIMISTIC_WRITE;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.NONE)) {
            lockMode = null;
        } else {
            throw new ConditionParametersException();
        }

        AccountEntity account;
        if (ObjectUtil.notNull(lockMode)) {
            account = this.entityManager.find(AccountEntity.class, id, lockMode);
        } else {
            account = this.entityManager.find(AccountEntity.class, id);
        }

        if (ObjectUtil.isNull(account)) {
            throw new StatusNotExistedException();
        }

        return account;
    }

    public AccountEntity getAccount(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountEntity> criteriaQuery = criteriaBuilder.createQuery(AccountEntity.class);
        Root<AccountEntity> root = criteriaQuery.from(AccountEntity.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("name"), name));
        TypedQuery<AccountEntity> typedQuery = this.entityManager.createQuery(criteriaQuery);
        List<AccountEntity> accounts = typedQuery.getResultList();

        if (accounts.isEmpty()) {
            throw new StatusNotExistedException();
        }

        AccountEntity account = accounts.getFirst();

        return account;
    }

    public AccountEntity getAccount(String name, long lock) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode;
        if (LogicalUtil.isAnyEqual(lock, LockType.READ)) {
            lockMode = LockModeType.PESSIMISTIC_READ;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.WRITE)) {
            lockMode = LockModeType.PESSIMISTIC_WRITE;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.NONE)) {
            lockMode = null;
        } else {
            throw new ConditionParametersException();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountEntity> criteriaQuery = criteriaBuilder.createQuery(AccountEntity.class);
        Root<AccountEntity> root = criteriaQuery.from(AccountEntity.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("name"), name));
        TypedQuery<AccountEntity> typedQuery = this.entityManager.createQuery(criteriaQuery);
        if (ObjectUtil.notNull(lockMode)) {
            typedQuery.setLockMode(lockMode);
        }
        List<AccountEntity> accounts = typedQuery.getResultList();

        if (accounts.isEmpty()) {
            throw new StatusNotExistedException();
        }

        return accounts.getFirst();
    }

    public GroupEntity getGroup(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        GroupEntity group = this.entityManager.find(GroupEntity.class, id);

        if (ObjectUtil.isNull(group)) {
            throw new StatusNotExistedException();
        }

        return group;
    }

    public GroupEntity getGroup(UUID id, long lock) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode;
        if (LogicalUtil.isAnyEqual(lock, LockType.READ)) {
            lockMode = LockModeType.PESSIMISTIC_READ;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.WRITE)) {
            lockMode = LockModeType.PESSIMISTIC_WRITE;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.NONE)) {
            lockMode = null;
        } else {
            throw new ConditionParametersException();
        }

        GroupEntity group;
        if (ObjectUtil.notNull(lockMode)) {
            group = this.entityManager.find(GroupEntity.class, id, lockMode);
        } else {
            group = this.entityManager.find(GroupEntity.class, id);
        }

        if (ObjectUtil.isNull(group)) {
            throw new StatusNotExistedException();
        }

        return group;
    }

    public GroupEntity getGroup(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GroupEntity> criteriaQuery = criteriaBuilder.createQuery(GroupEntity.class);
        Root<GroupEntity> root = criteriaQuery.from(GroupEntity.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("name"), name));
        TypedQuery<GroupEntity> typedQuery = this.entityManager.createQuery(criteriaQuery);
        List<GroupEntity> groups = typedQuery.getResultList();

        if (groups.isEmpty()) {
            throw new StatusNotExistedException();
        }

        GroupEntity group = groups.getFirst();

        return group;
    }

    public GroupEntity getGroup(String name, long lock) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode;
        if (LogicalUtil.isAnyEqual(lock, LockType.READ)) {
            lockMode = LockModeType.PESSIMISTIC_READ;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.WRITE)) {
            lockMode = LockModeType.PESSIMISTIC_WRITE;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.NONE)) {
            lockMode = null;
        } else {
            throw new ConditionParametersException();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GroupEntity> criteriaQuery = criteriaBuilder.createQuery(GroupEntity.class);
        Root<GroupEntity> root = criteriaQuery.from(GroupEntity.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("name"), name));
        TypedQuery<GroupEntity> typedQuery = this.entityManager.createQuery(criteriaQuery);
        if (ObjectUtil.notNull(lockMode)) {
            typedQuery.setLockMode(lockMode);
        }
        List<GroupEntity> groups = typedQuery.getResultList();

        if (groups.isEmpty()) {
            throw new StatusNotExistedException();
        }

        GroupEntity group = groups.getFirst();

        return group;
    }

    public AccountEntity add(AccountEntity account) {
        if (ObjectUtil.isNull(account)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(account)) {
            throw new StatusAlreadyExistedException();
        }

        return this.entityManager.merge(account);
    }

    public GroupEntity add(GroupEntity group) {
        if (ObjectUtil.isNull(group)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(group)) {
            throw new StatusAlreadyExistedException();
        }

        return this.entityManager.merge(group);
    }

    public void delete(AccountEntity account) {
        if (ObjectUtil.isNull(account)) {
            throw new ConditionParametersException();
        }

        if (!this.entityManager.contains(account)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(account);
    }

    public void delete(GroupEntity group) {
        if (ObjectUtil.isNull(group)) {
            throw new ConditionParametersException();
        }

        if (!this.entityManager.contains(group)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(group);
    }
}
