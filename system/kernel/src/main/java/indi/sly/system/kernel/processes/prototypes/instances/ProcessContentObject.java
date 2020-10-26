package indi.sly.system.kernel.processes.prototypes.instances;

import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.security.TokenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] value) {
    }

    @Override
    protected byte[] write() {
        return null;
    }

    private UUID parentProcessID;

    private long status;
    private long flag;


    private HandleTableDefinition handleTable;
    private PortDefinition port;
    private UUID sessionID;
    private StatisticsDefinition statistics;

    private TokenDefinition token;

    public UUID getParentProcessID() {
        return parentProcessID;
    }

    public void setParentProcessID(UUID parentProcessID) {
        this.parentProcessID = parentProcessID;
    }

    public HandleTableDefinition getHandleTable() {
        return handleTable;
    }

    public void setHandleTable(HandleTableDefinition handleTable) {
        this.handleTable = handleTable;
    }

    public PortDefinition getPort() {
        return port;
    }

    public void setPort(PortDefinition port) {
        this.port = port;
    }

    public UUID getSessionID() {
        return sessionID;
    }

    public void setSessionID(UUID sessionID) {
        this.sessionID = sessionID;
    }

    public StatisticsDefinition getStatistics() {
        return statistics;
    }

    public void setStatistics(StatisticsDefinition statistics) {
        this.statistics = statistics;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public TokenDefinition getToken() {
        return token;
    }

    public void setToken(TokenDefinition token) {
        this.token = token;
    }
}
