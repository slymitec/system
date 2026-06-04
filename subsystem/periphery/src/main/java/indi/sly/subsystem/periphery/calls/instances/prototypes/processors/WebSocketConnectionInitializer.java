package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.instances.prototypes.values.WebSocketConnectionStatusExtensionDefinition;
import indi.sly.subsystem.periphery.calls.prototypes.ConnectionObject;
import indi.sly.subsystem.periphery.calls.values.*;
import indi.sly.subsystem.periphery.core.prototypes.processors.AInitializer;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ObjectUtil;
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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebSocketConnectionInitializer extends AConnectionInitializer {
    @Override
    public synchronized void connect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        WebSocketConnectionStatusExtensionDefinition webSocketConnectionStatusExtension;
        WebSocketClient webSocketClient;

        if (ObjectUtil.allNotNull(status.getExtension())) {
            if (status.getExtension() instanceof WebSocketConnectionStatusExtensionDefinition) {
                webSocketConnectionStatusExtension = (WebSocketConnectionStatusExtensionDefinition) status.getExtension();

                webSocketClient = webSocketConnectionStatusExtension.getWebSocketClient();

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

            webSocketConnectionStatusExtension.setExecutor(Executors.newCachedThreadPool());

            webSocketClient = new WebSocketClient(address, new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshakeData) {
                }

                @Override
                public void onMessage(String message) {
                    ClientResponseDefinition userContentResponse = ObjectUtil.transferFromStringOrDefaultProvider(ClientResponseDefinition.class, message, () -> {
                        throw new StatusUnreadableException();
                    });

                    Map<UUID, ClientResponseDefinition> responses = webSocketConnectionStatusExtension.getResponses();
                    Map<UUID, Lock> locks = webSocketConnectionStatusExtension.getLocks();
                    Map<UUID, Condition> conditions = webSocketConnectionStatusExtension.getConditions();

                    UUID id;
                    if (ObjectUtil.allNotNull(userContentResponse.getException())) {
                        id = userContentResponse.getException().getId();
                    } else {
                        id = userContentResponse.getContent().getId();
                    }

                    Lock lock = locks.getOrDefault(id, null);
                    if (ObjectUtil.allNotNull(lock)) {
                        lock.lock();
                        try {
                            responses.put(id, userContentResponse);
                            locks.remove(id);
                            Condition condition = conditions.remove(id);

                            if (ObjectUtil.allNotNull(condition)) {
                                condition.signalAll();
                            }
                        } finally {
                            lock.unlock();
                        }
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    ConnectionObject connection = status.getConnection();
                    connection.disconnect();
                }

                @Override
                public void onError(Exception ex) {
                }
            };
            webSocketClient.connect();
            webSocketConnectionStatusExtension.setWebSocketClient(webSocketClient);

            status.setExtension(webSocketConnectionStatusExtension);
        }
    }

    @Override
    public void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        WebSocketConnectionStatusExtensionDefinition webSocketConnectionStatusExtension;

        if (ObjectUtil.isAnyNull(status.getExtension()) || !(status.getExtension() instanceof WebSocketConnectionStatusExtensionDefinition)) {
            throw new StatusRelationshipErrorException();
        } else {
            webSocketConnectionStatusExtension = (WebSocketConnectionStatusExtensionDefinition) status.getExtension();
        }

        webSocketConnectionStatusExtension.getExecutor().shutdown();
        try {
            if (webSocketConnectionStatusExtension.getExecutor().awaitTermination(1, TimeUnit.SECONDS)) {
                webSocketConnectionStatusExtension.getExecutor().shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        webSocketConnectionStatusExtension.setExecutor(null);

        WebSocketClient webSocketClient = webSocketConnectionStatusExtension.getWebSocketClient();
        if (webSocketClient.isOpen()) {
            webSocketClient.close();
        }
        webSocketConnectionStatusExtension.setWebSocketClient(null);

        webSocketConnectionStatusExtension.getLocks().clear();
        webSocketConnectionStatusExtension.getConditions().clear();
        webSocketConnectionStatusExtension.getResponses().clear();

        status.setExtension(null);
    }

    @Override
    public ClientResponseDefinition call(ClientRequestDefinition userContextRequest, ConnectionStatusDefinition status) {
        WebSocketConnectionStatusExtensionDefinition webSocketConnectionStatusExtension;

        if (ObjectUtil.isAnyNull(status.getExtension()) || !(status.getExtension() instanceof WebSocketConnectionStatusExtensionDefinition)) {
            throw new StatusRelationshipErrorException();
        } else {
            webSocketConnectionStatusExtension = (WebSocketConnectionStatusExtensionDefinition) status.getExtension();
        }

        WebSocketClient webSocketClient = webSocketConnectionStatusExtension.getWebSocketClient();

        UserContentRequestDefinition userContentRequest = userContextRequest.getContent();
        UUID id = userContentRequest.getId();

        Map<UUID, Lock> locks = webSocketConnectionStatusExtension.getLocks();
        Map<UUID, Condition> conditions = webSocketConnectionStatusExtension.getConditions();

        Lock newLock = new ReentrantLock();
        Condition newCondition = newLock.newCondition();
        locks.put(id, newLock);
        conditions.put(id, newCondition);

        webSocketClient.send(ObjectUtil.transferToString(userContextRequest));

        Map<UUID, ClientResponseDefinition> responses = webSocketConnectionStatusExtension.getResponses();

        Future<ClientResponseDefinition> userContentResponseFuture = webSocketConnectionStatusExtension.getExecutor().submit(() -> {
            Lock lock = locks.getOrDefault(id, null);

            ClientResponseDefinition userContentResponse = new ClientResponseDefinition();

            if (ObjectUtil.allNotNull(lock)) {
                lock.lock();
                try {
                    Condition condition = conditions.getOrDefault(id, null);

                    while (locks.containsKey(id)) {
                        if (condition.await(8, TimeUnit.SECONDS)) {
                            if (responses.containsKey(id)) {
                                userContentResponse = responses.remove(id);
                                locks.remove(id);
                                conditions.remove(id);
                            }
                        } else {
                            userContentResponse = responses.remove(id);
                            locks.remove(id);
                            conditions.remove(id);
                        }
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                userContentResponse = responses.remove(id);
            }

            return userContentResponse;
        });

        ClientResponseDefinition userContentResponse;
        try {
            userContentResponse = userContentResponseFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new StatusUnexpectedException();
        }
        if (ObjectUtil.isAnyNull(userContentResponse)) {
            throw new StatusUnexpectedException();
        }

        return userContentResponse;
    }
}
