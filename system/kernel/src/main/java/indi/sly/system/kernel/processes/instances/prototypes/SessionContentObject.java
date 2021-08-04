package indi.sly.system.kernel.processes.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.processes.instances.values.SessionDefinition;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SessionContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] source) {
        this.session = ObjectUtil.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtil.transferToByteArray(this.session);
    }

    private SessionDefinition session;

    public long getType() {
        this.init();

        return this.session.getType();
    }

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
        this.init();

        return this.session.getAccountID();
    }

    public void setAccountID(UUID accountID) {
        try {
            this.lock(LockType.WRITE);
            this.init();

            this.session.setAccountID(accountID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Set<UUID> getProcessIDs() {
        this.init();

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.getAccountID().equals(this.session.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        return CollectionUtil.unmodifiable(this.session.getProcessIDs());
    }

    public void addProcessID(UUID processID) {
        boolean result = false;

        try {
            this.lock(LockType.WRITE);
            this.init();

            result = this.session.getProcessIDs().add(processID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        if (!result) {
            throw new StatusAlreadyExistedException();
        }
    }

    public void deleteProcessID(UUID processID) {
        boolean result = false;

        try {
            this.lock(LockType.WRITE);
            this.init();

            result = this.session.getProcessIDs().remove(processID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        if (!result) {
            throw new StatusNotExistedException();
        }
    }

    public Map<String, String> getEnvironmentVariables() {
        this.init();

        return session.getEnvironmentVariables();
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
        this.init();

        return session.getParameters();
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
