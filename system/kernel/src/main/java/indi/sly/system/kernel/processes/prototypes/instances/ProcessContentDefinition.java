package indi.sly.system.kernel.processes.prototypes.instances;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.kernel.security.TokenDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ProcessContentDefinition implements ISerializable {
    public ProcessContentDefinition() {
        this.handleTable = new HandleTableDefinition();
        this.communication = new CommunicationDefinition();
        this.statistics = new StatisticsDefinition();
        this.status = new StatusDefintion();
        this.token = new TokenDefinition();
    }

    private CommunicationDefinition communication;
    private HandleTableDefinition handleTable;
    private StatusDefintion status;
    private StatisticsDefinition statistics;
    private TokenDefinition token;

    public CommunicationDefinition getCommunication() {
        return this.communication;
    }

    public HandleTableDefinition getHandleTable() {
        return this.handleTable;
    }

    public StatusDefintion getStatus() {
        return this.status;
    }

    public StatisticsDefinition getStatistics() {
        return this.statistics;
    }

    public TokenDefinition getToken() {
        return this.token;
    }


    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
    }
}
