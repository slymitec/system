package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.instances.prototypes.values.WebSocketConnectionStatusExtensionDefinition;
import indi.sly.subsystem.periphery.calls.values.*;
import indi.sly.system.common.lang.*;
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
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebSocketConnectionInitializer extends AConnectionInitializer {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    private void cancelAllPendingRequests(Map<UUID, CompletableFuture<ClientResponseDefinition>> pendingRequests) {
        if (pendingRequests.isEmpty()) {
            return;
        }
        CompletableFuture<?>[] futures = pendingRequests.values().toArray(new CompletableFuture<?>[0]);
        pendingRequests.clear();
        for (CompletableFuture<?> future : futures) {
            if (ObjectUtil.allNotNull(future)) {
                future.cancel(true);
            }
        }
    }

    @Override
    public void connect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        writeLock.lock();
        try {
            WebSocketConnectionStatusExtensionDefinition webSocketConnectionStatusExtension;
            WebSocketClient webSocketClient;

            if (ObjectUtil.allNotNull(status.getExtension())) {
                if (!(status.getExtension() instanceof WebSocketConnectionStatusExtensionDefinition)) {
                    throw new StatusRelationshipErrorException();
                }
                webSocketConnectionStatusExtension = (WebSocketConnectionStatusExtensionDefinition) status.getExtension();
                cancelAllPendingRequests(webSocketConnectionStatusExtension.getPendingRequests());

                webSocketClient = webSocketConnectionStatusExtension.getWebSocketClient();
                if (ObjectUtil.isAnyNull(webSocketClient)) {
                    throw new StatusRelationshipErrorException();
                }
                webSocketClient.reconnect();
            } else {
                URI address;
                try {
                    address = new URI(connection.getAddress());
                } catch (URISyntaxException e) {
                    throw new ConditionParametersException();
                }

                webSocketConnectionStatusExtension = new WebSocketConnectionStatusExtensionDefinition();
                final Map<UUID, CompletableFuture<ClientResponseDefinition>> pendingRequests =
                        webSocketConnectionStatusExtension.getPendingRequests();

                webSocketClient = new WebSocketClient(address, new Draft_6455()) {
                    @Override
                    public void onOpen(ServerHandshake handshakeData) {
                    }

                    @Override
                    public void onMessage(String message) {
                        UUID id = null;
                        try {
                            ClientResponseDefinition response =
                                    ObjectUtil.transferFromStringOrDefaultProvider(
                                            ClientResponseDefinition.class, message, () -> {
                                                throw new StatusUnreadableException();
                                            });

                            if (ObjectUtil.allNotNull(response.getException())) {
                                id = response.getException().getId();
                            } else if (ObjectUtil.allNotNull(response.getContent())) {
                                id = response.getContent().getId();
                            }

                            if (ValueUtil.isAnyNullOrEmpty(id)) {
                                return;
                            }

                            CompletableFuture<ClientResponseDefinition> future = pendingRequests.remove(id);
                            if (ObjectUtil.allNotNull(future)) {
                                future.complete(response);
                            }
                        } catch (Exception e) {
                            if (ObjectUtil.allNotNull(id)) {
                                CompletableFuture<ClientResponseDefinition> future = pendingRequests.remove(id);
                                if (ObjectUtil.allNotNull(future)) {
                                    future.completeExceptionally(new StatusUnreadableException());
                                }
                            }
                        }
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        cancelAllPendingRequests(pendingRequests);
                    }

                    @Override
                    public void onError(Exception ex) {
                        cancelAllPendingRequests(pendingRequests);
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
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        writeLock.lock();
        try {
            if (!(status.getExtension() instanceof WebSocketConnectionStatusExtensionDefinition ext)) {
                return;
            }

            cancelAllPendingRequests(ext.getPendingRequests());

            WebSocketClient webSocketClient = ext.getWebSocketClient();
            if (ObjectUtil.allNotNull(webSocketClient)) {
                try {
                    webSocketClient.close();
                } catch (Exception ignored) {
                }
                ext.setWebSocketClient(null);
            }
            status.setExtension(null);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public ClientResponseDefinition call(ClientRequestDefinition request, ConnectionStatusDefinition status) {
        readLock.lock();
        try {
            if (!(status.getExtension() instanceof WebSocketConnectionStatusExtensionDefinition ext)) {
                throw new StatusRelationshipErrorException();
            }

            WebSocketClient webSocketClient = ext.getWebSocketClient();
            if (ObjectUtil.isAnyNull(webSocketClient)) {
                throw new StatusRelationshipErrorException();
            }

            if (!webSocketClient.isOpen()) {
                throw new StatusUnexpectedException();
            }

            UserContentRequestDefinition userContentRequest = request.getContent();
            UUID id = userContentRequest.getId();
            if (ValueUtil.isAnyNullOrEmpty(id)) {
                throw new ConditionParametersException();
            }

            Map<UUID, CompletableFuture<ClientResponseDefinition>> pendingRequests = ext.getPendingRequests();
            CompletableFuture<ClientResponseDefinition> future = new CompletableFuture<>();
            pendingRequests.put(id, future);

            try {
                String message = ObjectUtil.transferToString(request);
                webSocketClient.send(message);
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
            } catch (TimeoutException e) {
                future.cancel(true);
                throw new StatusUnexpectedException();
            } catch (ExecutionException e) {
                future.cancel(true);
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
                throw new StatusUnexpectedException();
            } catch (CancellationException e) {
                throw new StatusUnexpectedException();
            } catch (InterruptedException e) {
                future.cancel(true);
                Thread.currentThread().interrupt();
                throw new StatusUnexpectedException();
            } finally {
                pendingRequests.remove(id);
            }
        } finally {
            readLock.unlock();
        }
    }
}