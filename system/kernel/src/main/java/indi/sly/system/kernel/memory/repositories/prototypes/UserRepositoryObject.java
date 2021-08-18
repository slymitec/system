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

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

        return ObjectUtil.isAnyNull(account);
    }

    public boolean containGroup(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        GroupEntity group = this.entityManager.find(GroupEntity.class, id);

        return ObjectUtil.isAnyNull(group);
    }

    public AccountEntity getAccount(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        AccountEntity account = this.entityManager.find(AccountEntity.class, id);

        if (ObjectUtil.isAnyNull(account)) {
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
        criteriaQuery.where(criteriaBuilder.equal(root.get("Name"), name));
        TypedQuery<AccountEntity> typedQuery = this.entityManager.createQuery(criteriaQuery);
        List<AccountEntity> accounts = typedQuery.getResultList();

        if (accounts.isEmpty()) {
            throw new StatusNotExistedException();
        }

        AccountEntity account = accounts.get(0);

        return account;
    }

    public GroupEntity getGroup(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        GroupEntity group = this.entityManager.find(GroupEntity.class, id);

        if (ObjectUtil.isAnyNull(group)) {
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
        criteriaQuery.where(criteriaBuilder.equal(root.get("Name"), name));
        TypedQuery<GroupEntity> typedQuery = this.entityManager.createQuery(criteriaQuery);
        List<GroupEntity> groups = typedQuery.getResultList();

        if (groups.isEmpty()) {
            throw new StatusNotExistedException();
        }

        GroupEntity group = groups.get(0);

        return group;
    }

    public void add(AccountEntity account) {
        if (ObjectUtil.isAnyNull(account)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(account)) {
            throw new StatusAlreadyExistedException();
        }

        this.entityManager.merge(account);
    }

    public void add(GroupEntity group) {
        if (ObjectUtil.isAnyNull(group)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(group)) {
            throw new StatusAlreadyExistedException();
        }

        this.entityManager.merge(group);
    }

    public void delete(AccountEntity account) {
        if (ObjectUtil.isAnyNull(account)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(account)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(account);
    }

    public void delete(GroupEntity group) {
        if (ObjectUtil.isAnyNull(group)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(group)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(group);
    }

    public void lock(AccountEntity account, long lock) {
        if (ObjectUtil.isAnyNull(account)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode;
        if (LogicalUtil.isAnyEqual(lock, LockType.READ)) {
            lockMode = LockModeType.PESSIMISTIC_READ;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.WRITE)) {
            lockMode = LockModeType.PESSIMISTIC_WRITE;
        } else {
            lockMode = LockModeType.NONE;
        }

        this.entityManager.lock(account, lockMode);
    }

    public void lock(GroupEntity group, long lock) {
        if (ObjectUtil.isAnyNull(group)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode;
        if (LogicalUtil.isAnyEqual(lock, LockType.READ)) {
            lockMode = LockModeType.PESSIMISTIC_READ;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.WRITE)) {
            lockMode = LockModeType.PESSIMISTIC_WRITE;
        } else {
            lockMode = LockModeType.NONE;
        }

        this.entityManager.lock(group, lockMode);
    }
}
