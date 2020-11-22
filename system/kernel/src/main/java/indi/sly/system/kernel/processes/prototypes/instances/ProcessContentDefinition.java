package indi.sly.system.kernel.processes.prototypes.instances;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleTableDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ProcessContentDefinition implements ISerializable<ProcessContentDefinition> {
    public ProcessContentDefinition() {
        this.handleTable = new ProcessHandleTableDefinition();
        this.communication = new CommunicationDefinition();
        this.statistics = new StatisticsDefinition();
        this.status = new ProcessStatusDefintion();
        this.token = new ProcessTokenDefinition();
    }

    private final CommunicationDefinition communication;
    private final ProcessHandleTableDefinition handleTable;
    private final ProcessStatusDefintion status;
    private final StatisticsDefinition statistics;
    private final ProcessTokenDefinition token;

    public CommunicationDefinition getCommunication() {
        return this.communication;
    }

    public ProcessHandleTableDefinition getHandleTable() {
        return this.handleTable;
    }

    public ProcessStatusDefintion getStatus() {
        return this.status;
    }

    public StatisticsDefinition getStatistics() {
        return this.statistics;
    }

    public ProcessTokenDefinition getToken() {
        return this.token;
    }

    @Override
    public Object clone() {
        return this.deepClone();
    }

    @Override
    public ProcessContentDefinition deepClone() {
        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
    }

}
