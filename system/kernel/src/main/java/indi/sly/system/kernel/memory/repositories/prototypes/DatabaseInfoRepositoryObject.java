package indi.sly.system.kernel.memory.repositories.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoRelationEntity;
import indi.sly.system.kernel.objects.values.InfoWildcardDefinition;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DatabaseInfoRepositoryObject extends AInfoRepositoryObject {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean contain(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.entityManager.find(InfoEntity.class, id);

        return ObjectUtil.allNotNull(info);
    }

    @Override
    public InfoEntity get(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.entityManager.find(InfoEntity.class, id);

        if (ObjectUtil.isAnyNull(info)) {
            throw new StatusNotExistedException();
        }

        return info;
    }

    @Override
    public InfoEntity add(InfoEntity info) {
        if (ObjectUtil.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(info)) {
            throw new StatusAlreadyExistedException();
        }

        return this.entityManager.merge(info);
    }

    @Override
    public void delete(InfoEntity info) {
        if (ObjectUtil.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        if (!this.entityManager.contains(info)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(info);
    }

    @Override
    public void lock(InfoEntity info, long lock) {
        if (ObjectUtil.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode = this.entityManager.getLockMode(info);

        if (lockMode == LockModeType.OPTIMISTIC_FORCE_INCREMENT) {
            return;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.READ)) {
            if (lockMode == LockModeType.PESSIMISTIC_READ || lockMode == LockModeType.PESSIMISTIC_WRITE) {
                return;
            } else {
                lockMode = LockModeType.PESSIMISTIC_READ;
            }
        } else if (LogicalUtil.isAnyEqual(lock, LockType.WRITE)) {
            if (lockMode == LockModeType.PESSIMISTIC_WRITE) {
                return;
            } else {
                lockMode = LockModeType.PESSIMISTIC_WRITE;
            }
        }

        this.entityManager.lock(info, lockMode);

        List<InfoRelationEntity> relations = this.listRelation(info, null);
        for (InfoRelationEntity relation : relations) {
            this.entityManager.lock(relation, lockMode);
        }
    }

    @Override
    public void unlock(InfoEntity info, long lock) {
        if (ObjectUtil.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode = this.entityManager.getLockMode(info);

        if (lockMode == LockModeType.OPTIMISTIC_FORCE_INCREMENT) {
            return;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.READ, LockType.WRITE)) {
            if (lockMode == LockModeType.PESSIMISTIC_READ || lockMode == LockModeType.PESSIMISTIC_WRITE) {
                return;
            } else {
                lockMode = LockModeType.NONE;
            }
        }

        this.entityManager.lock(info, lockMode);

        List<InfoRelationEntity> relations = this.listRelation(info, null);
        for (InfoRelationEntity relation : relations) {
            this.entityManager.lock(relation, lockMode);
        }
    }

    @Override
    public InfoRelationEntity getRelation(InfoEntity info, UUID id) {
        if (ObjectUtil.isAnyNull(info) || ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<InfoRelationEntity> criteriaQuery = criteriaBuilder.createQuery(InfoRelationEntity.class);
        Root<InfoRelationEntity> root = criteriaQuery.from(InfoRelationEntity.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("parentID"), info.getID()));
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
        TypedQuery<InfoRelationEntity> typedQuery = this.entityManager.createQuery(criteriaQuery);
        List<InfoRelationEntity> relations = typedQuery.getResultList();

        if (relations.isEmpty()) {
            throw new StatusNotExistedException();
        }

        return relations.getFirst();
    }

    @Override
    public InfoRelationEntity getRelation(InfoEntity info, String name) {
        if (ObjectUtil.isAnyNull(info) || StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<InfoRelationEntity> criteriaQuery = criteriaBuilder.createQuery(InfoRelationEntity.class);
        Root<InfoRelationEntity> root = criteriaQuery.from(InfoRelationEntity.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("parentID"), info.getID()));
        criteriaQuery.where(criteriaBuilder.equal(root.get("name"), name));
        TypedQuery<InfoRelationEntity> typedQuery = this.entityManager.createQuery(criteriaQuery);
        List<InfoRelationEntity> relations = typedQuery.getResultList();

        if (relations.isEmpty()) {
            throw new StatusNotExistedException();
        }

        return relations.getFirst();
    }

    @Override
    public List<InfoRelationEntity> listRelation(InfoEntity info, InfoWildcardDefinition wildcard) {
        if (ObjectUtil.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<InfoRelationEntity> criteriaQuery = criteriaBuilder.createQuery(InfoRelationEntity.class);
        Root<InfoRelationEntity> root = criteriaQuery.from(InfoRelationEntity.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("parentID"), info.getID()));
        if (!ValueUtil.isAnyNullOrEmpty(wildcard)) {
            if (wildcard.isFuzzy()) {
                if (wildcard.getType() == String.class) {
                    String wildcardValue = StringUtil.readFormBytes(wildcard.getValue());
                    wildcardValue = wildcardValue.replace("[", "[[]");
                    wildcardValue = wildcardValue.replace("%", "[%]");
                    wildcardValue = wildcardValue.replace("_", "[_]");
                    wildcardValue = wildcardValue.replace('*', '%');
                    wildcardValue = wildcardValue.replace('?', '_');
                    criteriaQuery.where(criteriaBuilder.like(root.get("name"), wildcardValue));
                }
            } else {
                if (wildcard.getType() == String.class) {
                    criteriaQuery.where(criteriaBuilder.equal(root.get("name"), StringUtil.readFormBytes(wildcard.getValue())));
                } else if (wildcard.getType() == UUID.class) {
                    criteriaQuery.where(criteriaBuilder.equal(root.get("id"), UUIDUtil.readFormBytes(wildcard.getValue())));
                }
            }
        }
        TypedQuery<InfoRelationEntity> typedQuery = this.entityManager.createQuery(criteriaQuery);
        List<InfoRelationEntity> relations = typedQuery.getResultList();

        return relations;
    }

    @Override
    public int countRelation(InfoEntity info, InfoWildcardDefinition wildcard) {
        if (ObjectUtil.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<InfoRelationEntity> root = criteriaQuery.from(InfoRelationEntity.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("parentID"), info.getID()));
        if (!ValueUtil.isAnyNullOrEmpty(wildcard)) {
            if (wildcard.isFuzzy()) {
                if (wildcard.getType() == String.class) {
                    String wildcardValue = StringUtil.readFormBytes(wildcard.getValue());
                    wildcardValue = wildcardValue.replace("[", "[[]");
                    wildcardValue = wildcardValue.replace("%", "[%]");
                    wildcardValue = wildcardValue.replace("_", "[_]");
                    wildcardValue = wildcardValue.replace('*', '%');
                    wildcardValue = wildcardValue.replace('?', '_');
                    criteriaQuery.where(criteriaBuilder.like(root.get("name"), wildcardValue));
                }
            } else {
                if (wildcard.getType() == String.class) {
                    criteriaQuery.where(criteriaBuilder.equal(root.get("name"), StringUtil.readFormBytes(wildcard.getValue())));
                } else if (wildcard.getType() == UUID.class) {
                    criteriaQuery.where(criteriaBuilder.equal(root.get("id"), UUIDUtil.readFormBytes(wildcard.getValue())));
                }
            }
        }
        TypedQuery<Long> typedQuery = this.entityManager.createQuery(criteriaQuery);
        int relationCount = typedQuery.getSingleResult().intValue();

        return relationCount;
    }

    @Override
    public void addRelation(InfoRelationEntity relation) {
        if (ObjectUtil.isAnyNull(relation)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(relation)) {
            throw new StatusAlreadyExistedException();
        }

        this.entityManager.merge(relation);
    }

    @Override
    public void deleteRelation(InfoRelationEntity relation) {
        if (ObjectUtil.isAnyNull(relation)) {
            throw new ConditionParametersException();
        }

        if (!this.entityManager.contains(relation)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(relation);
    }
}
