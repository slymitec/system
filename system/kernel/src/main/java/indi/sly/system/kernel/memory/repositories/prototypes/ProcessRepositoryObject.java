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

        //this.logger.warn(".contain(" + id + ");");

        ProcessEntity process = this.entityManager.find(ProcessEntity.class, id);

        return ObjectUtil.allNotNull(process);
    }

    public ProcessEntity get(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        //this.logger.warn(".get(" + id + ");");

        ProcessEntity process = this.entityManager.find(ProcessEntity.class, id);

        if (ObjectUtil.isAnyNull(process)) {
            throw new StatusNotExistedException();
        }

        return process;
    }

    public ProcessEntity add(ProcessEntity process) {
        if (ObjectUtil.isAnyNull(process)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(process)) {
            throw new StatusAlreadyExistedException();
        }

        //this.logger.warn(".add(" + process.getID() + ");");

        return this.entityManager.merge(process);
    }

    public void delete(ProcessEntity process) {
        if (ObjectUtil.isAnyNull(process)) {
            throw new ConditionParametersException();
        }

        if (!this.entityManager.contains(process)) {
            throw new StatusNotExistedException();
        }

        //this.logger.warn(".delete(" + process.getID() + ");");

        this.entityManager.remove(process);
    }

    public void lock(ProcessEntity process, long lock) {
        if (ObjectUtil.isAnyNull(process)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode = this.entityManager.getLockMode(process);

        //this.logger.warn(".lock(" + process.getID() + ", " + lock + "); Current lockMode is " + lockMode);

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

        this.entityManager.lock(process, lockMode);
    }
}
