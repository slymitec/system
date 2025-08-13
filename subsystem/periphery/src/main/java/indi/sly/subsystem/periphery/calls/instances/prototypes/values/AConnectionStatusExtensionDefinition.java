package indi.sly.subsystem.periphery.calls.instances.prototypes.values;

import indi.sly.subsystem.periphery.calls.values.UserContentResponseDefinition;
import indi.sly.system.common.values.ADefinition;
import org.java_websocket.client.WebSocketClient;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public abstract class AConnectionStatusExtensionDefinition<T> extends ADefinition<T> {
    public AConnectionStatusExtensionDefinition() {
    }
}
