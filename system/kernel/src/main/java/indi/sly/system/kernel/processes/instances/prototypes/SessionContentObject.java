package indi.sly.system.kernel.processes.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.MethodScope;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.MethodScopeType;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
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
            this.lock(LockType.WRITE);
            this.init();

            return this.session.getName();
        } finally {
            this.lock(LockType.NONE);
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
            this.lock(LockType.NONE);
        }
    }

    public long getType() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            return this.session.getType();
        } finally {
            this.lock(LockType.NONE);
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
            this.lock(LockType.NONE);
        }
    }

    public UUID getAccountID() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            return this.session.getAccountID();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Set<UUID> listProcessID() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            return CollectionUtil.unmodifiable(this.session.getProcessIDs());
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Map<String, String> getEnvironmentVariables() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            return CollectionUtil.unmodifiable(this.session.getEnvironmentVariables());
        } finally {
            this.lock(LockType.NONE);
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
            this.lock(LockType.NONE);
        }
    }

    public Map<String, String> getParameters() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            return CollectionUtil.unmodifiable(this.session.getParameters());
        } finally {
            this.lock(LockType.NONE);
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
            this.lock(LockType.NONE);
        }
    }
}
