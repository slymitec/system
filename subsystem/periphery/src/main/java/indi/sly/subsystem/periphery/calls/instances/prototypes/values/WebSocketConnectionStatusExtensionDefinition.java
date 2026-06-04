package indi.sly.subsystem.periphery.calls.instances.prototypes.values;

import indi.sly.subsystem.periphery.calls.values.ClientResponseDefinition;
import org.java_websocket.client.WebSocketClient;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class WebSocketConnectionStatusExtensionDefinition extends AConnectionStatusExtensionDefinition {
    public WebSocketConnectionStatusExtensionDefinition() {
        this.pendingRequests = new ConcurrentHashMap<>();
    }

    private WebSocketClient webSocketClient;
    private final Map<UUID, CompletableFuture<ClientResponseDefinition>> pendingRequests;

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public void setWebSocketClient(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    public Map<UUID, CompletableFuture<ClientResponseDefinition>> getPendingRequests() {
        return pendingRequests;
    }
}