package indi.sly.system.kernel.memory.repositories.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoRelationEntity;
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
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        //this.logger.warn(".contain(" + id + ");");

        InfoEntity info = this.entityManager.find(InfoEntity.class, id);

        return ObjectUtil.allNotNull(info);
    }

    @Override
    public InfoEntity get(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        //this.logger.warn(".get(" + id + ");");

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

        //this.logger.warn(".add(" + info.getID() + ");");

        return this.entityManager.merge(info);
    }

    @Override
    public void delete(InfoEntity info) {
        if (ObjectUtil.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(info)) {
            throw new StatusNotExistedException();
        }

        //this.logger.warn(".delete(" + info.getID() + ");");

        this.entityManager.remove(info);
    }

    @Override
    public void lock(InfoEntity info, long lock) {
        if (ObjectUtil.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode = this.entityManager.getLockMode(info);

        //this.logger.warn(".lock(" + info.getID() + ", " + lock + "); Current lockMode is " + lockMode);

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
        } else {
            if (lockMode == LockModeType.PESSIMISTIC_READ || lockMode == LockModeType.PESSIMISTIC_WRITE) {
                return;
            } else {
                lockMode = LockModeType.NONE;
            }
        }

        this.entityManager.lock(info, lockMode);

        List<InfoRelationEntity> relations = this.listRelation(info);
        for (InfoRelationEntity relation : relations) {
            this.entityManager.lock(relation, lockMode);
        }
    }

    @Override
    public List<InfoRelationEntity> listRelation(InfoEntity info) {
        if (ObjectUtil.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        this.logger.warn(".listRelation(" + info.getID() + ");");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<InfoRelationEntity> criteriaQuery = criteriaBuilder.createQuery(InfoRelationEntity.class);
        Root<InfoRelationEntity> root = criteriaQuery.from(InfoRelationEntity.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("parentID"), info.getID()));
        TypedQuery<InfoRelationEntity> typedQuery = this.entityManager.createQuery(criteriaQuery);
        List<InfoRelationEntity> relations = typedQuery.getResultList();

        this.logger.warn("-.listRelation(" + relations.size() + ");");

        return relations;
    }

    @Override
    public void addRelation(InfoRelationEntity relation) {
        if (ObjectUtil.isAnyNull(relation)) {
            throw new ConditionParametersException();
        }

        this.logger.warn(".addRelation(" + relation.getID() + " " + relation.getParentID() + ");");

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

        //this.logger.warn(".deleteRelation(" + relation.getID() + " " + relation.getParentID() + ");");

        if (this.entityManager.contains(relation)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(relation);
    }
}
