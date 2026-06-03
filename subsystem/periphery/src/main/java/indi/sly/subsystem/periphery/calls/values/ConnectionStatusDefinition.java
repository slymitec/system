package indi.sly.subsystem.periphery.calls.values;

import indi.sly.subsystem.periphery.calls.instances.prototypes.values.AConnectionStatusExtensionDefinition;
import indi.sly.subsystem.periphery.calls.prototypes.ConnectionObject;
import indi.sly.system.common.values.ADefinition;

public class ConnectionStatusDefinition extends ADefinition {
    public ConnectionStatusDefinition() {
    }

    private ConnectionObject connection;
    private long runtime;
    private AConnectionStatusExtensionDefinition extension;

    public ConnectionObject getConnection() {
        return this.connection;
    }

    public void setConnection(ConnectionObject connection) {
        this.connection = connection;
    }

    public long getRuntime() {
        return this.runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public Object getExtension() {
        return this.extension;
    }

    public void setExtension(AConnectionStatusExtensionDefinition extension) {
        this.extension = extension;
    }
}
