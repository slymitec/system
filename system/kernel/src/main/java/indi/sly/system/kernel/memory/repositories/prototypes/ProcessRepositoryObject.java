package indi.sly.system.kernel.memory.repositories.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessRepositoryObject extends AObject {
    @PersistenceContext
    private EntityManager entityManager;

    public boolean contain(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ProcessEntity process = this.entityManager.find(ProcessEntity.class, id);

        return ObjectUtil.notNull(process);
    }

    public ProcessEntity get(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ProcessEntity process = this.entityManager.find(ProcessEntity.class, id);

        if (ObjectUtil.isNull(process)) {
            throw new StatusNotExistedException();
        }

        return process;
    }

    public ProcessEntity get(UUID id, long lock) {
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

        ProcessEntity process;
        if (ObjectUtil.notNull(lockMode)) {
            process = this.entityManager.find(ProcessEntity.class, id, lockMode);
        } else {
            process = this.entityManager.find(ProcessEntity.class, id);
        }

        if (ObjectUtil.isNull(process)) {
            throw new StatusNotExistedException();
        }

        return process;
    }

    public ProcessEntity add(ProcessEntity process) {
        if (ObjectUtil.isNull(process)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(process)) {
            throw new StatusAlreadyExistedException();
        }

        return this.entityManager.merge(process);
    }

    public void delete(ProcessEntity process) {
        if (ObjectUtil.isNull(process)) {
            throw new ConditionParametersException();
        }

        if (!this.entityManager.contains(process)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(process);
    }
}
