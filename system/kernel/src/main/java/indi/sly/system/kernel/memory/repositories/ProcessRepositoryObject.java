package indi.sly.system.kernel.memory.repositories;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.StatusAlreadyExistedException;
import indi.sly.system.common.exceptions.StatusNotExistedException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.ACoreObject;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoRelationEntity;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
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
public class ProcessRepositoryObject extends ACoreObject {
    @PersistenceContext
    private EntityManager entityManager;

    public boolean contain(UUID id) {
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ProcessEntity process = this.entityManager.find(ProcessEntity.class, id);

        return ObjectUtils.isAnyNull(process);
    }

    public ProcessEntity get(UUID id) {
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ProcessEntity process = this.entityManager.find(ProcessEntity.class, id);

        if (ObjectUtils.isAnyNull(process)) {
            throw new StatusNotExistedException();
        }

        return process;
    }

    public void add(ProcessEntity process) {
        if (ObjectUtils.isAnyNull(process)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(process)) {
            throw new StatusAlreadyExistedException();
        }

        this.entityManager.merge(process);
    }

    public void delete(ProcessEntity process) {
        if (ObjectUtils.isAnyNull(process)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(process)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(process);
    }

    public void lock(ProcessEntity process, long lockType) {
        if (ObjectUtils.isAnyNull(process)) {
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

        this.entityManager.lock(process, lockModeType);
    }
}
