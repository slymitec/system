package indi.sly.system.kernel.memory.repositories.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.services.values.ServiceStatusEntity;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceRepositoryObject extends AObject {
    @PersistenceContext
    private EntityManager entityManager;

    public boolean contain(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ServiceStatusEntity service = this.entityManager.find(ServiceStatusEntity.class, id);

        return ObjectUtil.allNotNull(service);
    }

    public ServiceStatusEntity get(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ServiceStatusEntity service = this.entityManager.find(ServiceStatusEntity.class, id);

        if (ObjectUtil.isAnyNull(service)) {
            throw new StatusNotExistedException();
        }

        return service;
    }

    public ServiceStatusEntity add(ServiceStatusEntity service) {
        if (ObjectUtil.isAnyNull(service)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(service)) {
            throw new StatusAlreadyExistedException();
        }

        return this.entityManager.merge(service);
    }

    public void delete(ServiceStatusEntity service) {
        if (ObjectUtil.isAnyNull(service)) {
            throw new ConditionParametersException();
        }

        if (!this.entityManager.contains(service)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(service);
    }

    public void lock(ServiceStatusEntity service, long lock) {
        if (ObjectUtil.isAnyNull(service)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode = this.entityManager.getLockMode(service);

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

        this.entityManager.lock(service, lockMode);
    }

    public void unlock(ServiceStatusEntity service, long lock) {
        if (ObjectUtil.isAnyNull(service)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode = this.entityManager.getLockMode(service);

        if (lockMode == LockModeType.OPTIMISTIC_FORCE_INCREMENT) {
            return;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.READ, LockType.WRITE)) {
            if (lockMode == LockModeType.PESSIMISTIC_READ || lockMode == LockModeType.PESSIMISTIC_WRITE) {
                return;
            } else {
                lockMode = LockModeType.NONE;
            }
        }

        this.entityManager.lock(service, lockMode);
    }
}
