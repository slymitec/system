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

public class WebSocketConnectionStatusHelperDefinition extends ADefinition<WebSocketConnectionStatusHelperDefinition> {
    public WebSocketConnectionStatusHelperDefinition() {
        this.locks = new ConcurrentHashMap<>();
        this.conditions = new ConcurrentHashMap<>();
        this.responses = new ConcurrentHashMap<>();
    }

    private ExecutorService executor;
    private WebSocketClient webSocketClient;
    private final Map<UUID, Lock> locks;
    private final Map<UUID, Condition> conditions;
    private final Map<UUID, UserContentResponseDefinition> responses;

    public ExecutorService getExecutor() {
        return this.executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public WebSocketClient getWebSocketClient() {
        return this.webSocketClient;
    }

    public void setWebSocketClient(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    public Map<UUID, Lock> getLocks() {
        return this.locks;
    }

    public Map<UUID, Condition> getConditions() {
        return this.conditions;
    }

    public Map<UUID, UserContentResponseDefinition> getResponses() {
        return this.responses;
    }
}
