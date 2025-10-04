package indi.sly.system.kernel.processes.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.MethodScope;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.MethodScopeType;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.instances.values.HandlerEntryDefinition;
import indi.sly.system.kernel.processes.instances.values.SessionDefinition;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SessionContentObject extends AInfoContentObject {
    public SessionContentObject() {
        this.funcCustomRead = () -> this.session = ObjectUtil.transferFromByteArray(this.value);
        this.funcCustomWrite = () -> this.value = ObjectUtil.transferToByteArray(this.session);
    }

    private SessionDefinition session;

    public String getName() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.session.getName();
        } finally {
            this.unlock(LockType.READ);
        }
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public void setName(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.session.setName(name);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }

    public long getType() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.session.getType();
        } finally {
            this.unlock(LockType.READ);
        }
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public void setType(long type) {
        try {
            this.lock(LockType.WRITE);
            this.init();

            this.session.setType(type);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }

    public UUID getAccountID() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.session.getAccountID();
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public Set<UUID> listProcessID() {
        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.session.getProcessIDs());
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public Set<UUID> listHandle() {
        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.session.getHandles().keySet());
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public UUID getKernelIDFromHandle(UUID handle) {
        try {
            this.lock(LockType.READ);
            this.init();

            HandlerEntryDefinition handlerEntry = this.session.getHandles().getOrDefault(handle, null);

            if (ObjectUtil.isAnyNull(handlerEntry)) {
                throw new StatusNotExistedException();
            }

            return handlerEntry.getKernelID();
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public void addHandle(UUID handle) {
        try {
            this.lock(LockType.WRITE);
            this.init();

            HandlerEntryDefinition handlerEntry = new HandlerEntryDefinition();
            handlerEntry.setHandlerID(handle);
            handlerEntry.setKernelID(this.getKernelID());

            this.session.getHandles().put(handle, handlerEntry);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }

    public void deleteHandle(UUID handle) {
        try {
            this.lock(LockType.WRITE);
            this.init();

            HandlerEntryDefinition handlerEntry = this.session.getHandles().getOrDefault(handle, null);

            if (ObjectUtil.isAnyNull(handlerEntry)) {
                throw new StatusNotExistedException();
            }

            this.session.getHandles().remove(handle);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }


    public Map<String, String> getEnvironmentVariables() {
        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.session.getEnvironmentVariables());
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public void setEnvironmentVariables(Map<String, String> environment) {
        if (ObjectUtil.isAnyNull(environment)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.session.getEnvironmentVariables().clear();
            this.session.getEnvironmentVariables().putAll(environment);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }

    public Map<String, String> getParameters() {
        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.session.getParameters());
        } finally {
            this.unlock(LockType.READ);
        }
    }

    public void setParameters(Map<String, String> parameters) {
        if (ObjectUtil.isAnyNull(parameters)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.session.getParameters().clear();
            this.session.getParameters().putAll(parameters);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }
}
