package indi.sly.system.kernel.memory.repositories;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.StatusAlreadyExistedException;
import indi.sly.system.common.exceptions.StatusNotExistedException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoRelationEntity;
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
public class DatabaseInfoRepositoryObject extends AInfoRepositoryObject {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean contain(UUID id) {
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.entityManager.find(InfoEntity.class, id);

        return ObjectUtils.isAnyNull(info);
    }

    @Override
    public InfoEntity get(UUID id) {
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.entityManager.find(InfoEntity.class, id);

        if (ObjectUtils.isAnyNull(info)) {
            throw new StatusNotExistedException();
        }

        return info;
    }

    @Override
    public void add(InfoEntity info) {
        if (ObjectUtils.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(info)) {
            throw new StatusAlreadyExistedException();
        }

        this.entityManager.merge(info);
    }

    @Override
    public void delete(InfoEntity info) {
        if (ObjectUtils.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(info)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(info);
    }

    @Override
    public void lock(InfoEntity info, long lockType) {
        if (ObjectUtils.isAnyNull(info)) {
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

        this.entityManager.lock(info, lockModeType);

        List<InfoRelationEntity> relations = this.listRelation(info);
        for (InfoRelationEntity relation : relations) {
            this.entityManager.lock(relation, lockModeType);
        }
    }

    @Override
    public List<InfoRelationEntity> listRelation(InfoEntity info) {
        if (ObjectUtils.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<InfoRelationEntity> criteriaQuery = criteriaBuilder.createQuery(InfoRelationEntity.class);
        Root<InfoRelationEntity> root = criteriaQuery.from(InfoRelationEntity.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("ParentID"), info.getID()));
        TypedQuery<InfoRelationEntity> typedQuery = this.entityManager.createQuery(criteriaQuery);
        List<InfoRelationEntity> relations = typedQuery.getResultList();

        return relations;
    }

    @Override
    public void addRelation(InfoRelationEntity relation) {
        if (ObjectUtils.isAnyNull(relation)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(relation)) {
            throw new StatusAlreadyExistedException();
        }

        this.entityManager.merge(relation);
    }

    @Override
    public void deleteRelation(InfoRelationEntity relation) {
        if (ObjectUtils.isAnyNull(relation)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(relation)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(relation);
    }
}
