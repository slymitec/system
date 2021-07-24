package indi.sly.system.kernel.memory.repositories.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessRepositoryObject extends APrototype {
    @PersistenceContext
    private EntityManager entityManager;

    public boolean contain(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ProcessEntity process = this.entityManager.find(ProcessEntity.class, id);

        return !ObjectUtil.isAnyNull(process);
    }

    public ProcessEntity get(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ProcessEntity process = this.entityManager.find(ProcessEntity.class, id);

        if (ObjectUtil.isAnyNull(process)) {
            throw new StatusNotExistedException();
        }

        return process;
    }

    public void add(ProcessEntity process) {
        if (ObjectUtil.isAnyNull(process)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(process)) {
            throw new StatusAlreadyExistedException();
        }

        this.entityManager.merge(process);
    }

    public void delete(ProcessEntity process) {
        if (ObjectUtil.isAnyNull(process)) {
            throw new ConditionParametersException();
        }

        if (this.entityManager.contains(process)) {
            throw new StatusNotExistedException();
        }

        this.entityManager.remove(process);
    }

    public void lock(ProcessEntity process, long lock) {
        if (ObjectUtil.isAnyNull(process)) {
            throw new ConditionParametersException();
        }

        LockModeType lockMode;
        if (lock == LockType.READ) {
            lockMode = LockModeType.PESSIMISTIC_READ;
        } else if (lock == LockType.WRITE) {
            lockMode = LockModeType.PESSIMISTIC_WRITE;
        } else {
            lockMode = LockModeType.NONE;
        }

        this.entityManager.lock(process, lockMode);
    }
}
