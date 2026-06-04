package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.instances.prototypes.values.WebSocketConnectionStatusExtensionDefinition;
import indi.sly.subsystem.periphery.calls.prototypes.ConnectionObject;
import indi.sly.subsystem.periphery.calls.values.*;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebSocketConnectionInitializer extends AConnectionInitializer {
    private final AtomicBoolean disconnecting = new AtomicBoolean(false);

    private void cancelAllPendingRequests(Map<UUID, CompletableFuture<ClientResponseDefinition>> pendingRequests) {
        CompletableFuture<?>[] futures = pendingRequests.values().toArray(new CompletableFuture<?>[0]);
        pendingRequests.clear();
        for (CompletableFuture<?> future : futures) {
            if (ObjectUtil.allNotNull(future)) {
                future.cancel(true);
            }
        }
    }

    @Override
    public synchronized void connect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        WebSocketConnectionStatusExtensionDefinition webSocketConnectionStatusExtension;
        WebSocketClient webSocketClient;

        if (ObjectUtil.allNotNull(status.getExtension())) {
            if (status.getExtension() instanceof WebSocketConnectionStatusExtensionDefinition) {
                webSocketConnectionStatusExtension = (WebSocketConnectionStatusExtensionDefinition) status.getExtension();

                cancelAllPendingRequests(webSocketConnectionStatusExtension.getPendingRequests());

                webSocketClient = webSocketConnectionStatusExtension.getWebSocketClient();
                if (ObjectUtil.isAnyNull(webSocketClient)) {
                    throw new StatusRelationshipErrorException();
                }
                webSocketClient.reconnect();
            } else {
                throw new StatusRelationshipErrorException();
            }
        } else {
            URI address;
            try {
                address = new URI(connection.getAddress());
            } catch (URISyntaxException e) {
                throw new ConditionParametersException();
            }

            webSocketConnectionStatusExtension = new WebSocketConnectionStatusExtensionDefinition();

            webSocketClient = new WebSocketClient(address, new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshakeData) {
                }

                @Override
                public void onMessage(String message) {
                    UUID id = null;
                    try {
                        ClientResponseDefinition userContentResponse =
                                ObjectUtil.transferFromStringOrDefaultProvider(
                                        ClientResponseDefinition.class, message, () -> {
                                            throw new StatusUnreadableException();
                                        });

                        if (ObjectUtil.allNotNull(userContentResponse.getException())) {
                            id = userContentResponse.getException().getId();
                        } else {
                            id = userContentResponse.getContent().getId();
                        }

                        if (ValueUtil.isAnyNullOrEmpty(id)) {
                            return;
                        }

                        Map<UUID, CompletableFuture<ClientResponseDefinition>> pendingRequests =
                                webSocketConnectionStatusExtension.getPendingRequests();
                        CompletableFuture<ClientResponseDefinition> future = pendingRequests.remove(id);
                        if (ObjectUtil.allNotNull(future)) {
                            future.complete(userContentResponse);
                        }
                    } catch (Exception _) {
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    cancelAllPendingRequests(webSocketConnectionStatusExtension.getPendingRequests());

                    ConnectionObject connection = status.getConnection();
                    if (ObjectUtil.allNotNull(connection)) {
                        connection.disconnect();
                    }
                }

                @Override
                public void onError(Exception ex) {
                }
            };

            try {
                webSocketClient.connect();
            } catch (Exception e) {
                try {
                    webSocketClient.close();
                } catch (Exception ignored) {
                }
                throw new StatusUnexpectedException();
            }

            webSocketConnectionStatusExtension.setWebSocketClient(webSocketClient);
            status.setExtension(webSocketConnectionStatusExtension);
        }
    }

    @Override
    public void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        if (!disconnecting.compareAndSet(false, true)) {
            return;
        }

        try {
            if (ObjectUtil.isAnyNull(status.getExtension())) {
                return;
            }

            WebSocketConnectionStatusExtensionDefinition webSocketConnectionStatusExtension;

            if (ObjectUtil.isAnyNull(status.getExtension())
                    || !(status.getExtension() instanceof WebSocketConnectionStatusExtensionDefinition)) {
                throw new StatusRelationshipErrorException();
            }
            webSocketConnectionStatusExtension = (WebSocketConnectionStatusExtensionDefinition) status.getExtension();

            cancelAllPendingRequests(webSocketConnectionStatusExtension.getPendingRequests());

            if (LogicalUtil.isAnyEqual(status.getRuntime(), ConnectStatusRuntimeType.CONNECTED)) {
                WebSocketClient webSocketClient = webSocketConnectionStatusExtension.getWebSocketClient();
                if (ObjectUtil.allNotNull(webSocketClient)) {
                    try {
                        webSocketClient.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        } finally {
            if (status.getExtension() instanceof WebSocketConnectionStatusExtensionDefinition ext) {
                ext.setWebSocketClient(null);
            }
            status.setExtension(null);
            disconnecting.set(false);
        }
    }

    @Override
    public ClientResponseDefinition call(ClientRequestDefinition userContextRequest, ConnectionStatusDefinition status) {
        WebSocketConnectionStatusExtensionDefinition webSocketConnectionStatusExtension;

        if (ObjectUtil.isAnyNull(status.getExtension())
                || !(status.getExtension() instanceof WebSocketConnectionStatusExtensionDefinition)) {
            throw new StatusRelationshipErrorException();
        }
        webSocketConnectionStatusExtension = (WebSocketConnectionStatusExtensionDefinition) status.getExtension();

        WebSocketClient webSocketClient = webSocketConnectionStatusExtension.getWebSocketClient();
        if (ObjectUtil.isAnyNull(webSocketClient)) {
            throw new StatusRelationshipErrorException();
        }

        UserContentRequestDefinition userContentRequest = userContextRequest.getContent();
        UUID id = userContentRequest.getId();

        Map<UUID, CompletableFuture<ClientResponseDefinition>> pendingRequests =
                webSocketConnectionStatusExtension.getPendingRequests();

        CompletableFuture<ClientResponseDefinition> future = new CompletableFuture<>();
        pendingRequests.put(id, future);

        try {
            webSocketClient.send(ObjectUtil.transferToString(userContextRequest));
        } catch (Exception e) {
            pendingRequests.remove(id);
            future.cancel(true);
            throw new StatusUnexpectedException();
        }

        try {
            ClientResponseDefinition response = future.get(8, TimeUnit.SECONDS);
            if (ObjectUtil.isAnyNull(response)) {
                throw new StatusUnexpectedException();
            }
            return response;
        } catch (TimeoutException | ExecutionException | CancellationException e) {
            throw new StatusUnexpectedException();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new StatusUnexpectedException();
        } finally {
            pendingRequests.remove(id);
        }
    }
}