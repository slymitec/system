package indi.sly.system.kernel.processes.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.IByteValueProcess;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.instances.values.SessionDefinition;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SessionContentObject extends AInfoContentObject implements IByteValueProcess<SessionDefinition> {
    public String getName() {
        SessionDefinition session = this.init(this.read());

        return session.getName();
    }

    public void setName(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        SessionDefinition session = this.init(this.read());

        session.setName(name);

        this.write(this.flush(session));
    }

    public long getType() {
        SessionDefinition session = this.init(this.read());

        return session.getType();
    }

    public void setType(long type) {
        SessionDefinition session = this.init(this.read());

        session.setType(type);

        this.write(this.flush(session));
    }

    public UUID getAccountID() {
        SessionDefinition session = this.init(this.read());

        return session.getAccountID();
    }

    public Set<UUID> listProcessID() {
        SessionDefinition session = this.init(this.read());

        return CollectionUtil.unmodifiable(session.getProcessIDs());
    }

    public Map<String, String> getEnvironmentVariables() {
        SessionDefinition session = this.init(this.read());

        return CollectionUtil.unmodifiable(session.getEnvironmentVariables());
    }

    public void setEnvironmentVariables(Map<String, String> environment) {
        if (ObjectUtil.isAnyNull(environment)) {
            throw new ConditionParametersException();
        }

        SessionDefinition session = this.init(this.read());

        session.getEnvironmentVariables().clear();
        session.getEnvironmentVariables().putAll(environment);

        this.write(this.flush(session));
    }

    public Map<String, String> getParameters() {
        SessionDefinition session = this.init(this.read());

        return CollectionUtil.unmodifiable(session.getParameters());
    }

    public void setParameters(Map<String, String> parameters) {
        if (ObjectUtil.isAnyNull(parameters)) {
            throw new ConditionParametersException();
        }

        SessionDefinition session = this.init(this.read());

        session.getParameters().clear();
        session.getParameters().putAll(parameters);

        this.write(this.flush(session));
    }
}
