package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AIndependentBytesValueProcessObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessSessionObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import indi.sly.system.kernel.security.values.UserSessionDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSessionObject extends AIndependentBytesValueProcessObject<UserSessionDefinition> {
    public long getLimit() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getLimit();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setLimit(long limit) {
        if (limit < 0L) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.SECURITY_MODIFY_ACCOUNT_AND_GROUP)) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setLimit(limit);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public int getSize() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getIDs().size();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Set<String> getAllNames() {
        try {
            this.lock(LockType.READ);
            this.init();

            return CollectionUtil.unmodifiable(this.value.getIDs().keySet());
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public UUID get(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            UUID sessionID = this.value.getIDs().getOrDefault(name, null);

            if (ValueUtil.isAnyNullOrEmpty(sessionID)) {
                throw new StatusNotExistedException();
            }

            return sessionID;
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void add(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessSessionObject processSession = process.getSession();
        UUID sessionID = processSession.getID();

        if (ValueUtil.isAnyNullOrEmpty(sessionID)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Map<String, UUID> ids = this.value.getIDs();

            if (ids.containsValue(sessionID)) {
                throw new StatusAlreadyExistedException();
            }
            if (ids.size() >= this.value.getLimit()) {
                throw new StatusInsufficientResourcesException();
            }

            ids.put(name, sessionID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void delete(UUID sessionID) {
        if (ValueUtil.isAnyNullOrEmpty(sessionID)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Map<String, UUID> ids = this.value.getIDs();

            String name = null;
            for (Map.Entry<String, UUID> pair : ids.entrySet()) {
                if (sessionID.equals(pair.getValue())) {
                    name = pair.getKey();
                    break;
                }
            }

            if (StringUtil.isNameIllegal(name)) {
                throw new StatusNotExistedException();
            }

            ids.remove(name);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
