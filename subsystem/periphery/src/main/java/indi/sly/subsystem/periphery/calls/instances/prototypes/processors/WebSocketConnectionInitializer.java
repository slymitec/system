package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.instances.prototypes.values.WebSocketConnectionStatusExtensionDefinition;
import indi.sly.subsystem.periphery.calls.prototypes.ConnectionObject;
import indi.sly.subsystem.periphery.calls.values.*;
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
        synchronized (this) {
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
                        UserContentResponseDefinition userContentResponse = ObjectUtil.transferFromStringOrDefaultProvider(UserContentResponseDefinition.class, message, () -> {
                            throw new StatusUnreadableException();
                        });

                        Map<UUID, UserContentResponseDefinition> responses = webSocketConnectionStatusExtension.getResponses();
                        Map<UUID, Lock> locks = webSocketConnectionStatusExtension.getLocks();
                        Map<UUID, Condition> conditions = webSocketConnectionStatusExtension.getConditions();

                        UUID id = userContentResponse.getID();

                        Lock lock = locks.getOrDefault(id, null);
                        if (ObjectUtil.allNotNull(lock)) {
                            try {
                                lock.lock();

                                responses.put(id, userContentResponse);
                                locks.remove(id);
                                conditions.remove(id);

                                Condition condition = conditions.getOrDefault(id, null);

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
                        connection.clone();
                    }

                    @Override
                    public void onError(Exception ex) {
                    }
                };
                webSocketConnectionStatusExtension.setWebSocketClient(webSocketClient);

                status.setExtension(webSocketConnectionStatusExtension);
            }
        }
    }

    @Override
    public synchronized void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        WebSocketConnectionStatusExtensionDefinition webSocketConnectionStatusExtension;

        if (ObjectUtil.isAnyNull(status.getExtension()) || status.getExtension() instanceof WebSocketConnectionStatusExtensionDefinition) {
            throw new StatusRelationshipErrorException();
        } else {
            webSocketConnectionStatusExtension = (WebSocketConnectionStatusExtensionDefinition) status.getExtension();
        }

        synchronized (this) {
            webSocketConnectionStatusExtension.getExecutor().shutdown();
            webSocketConnectionStatusExtension.setExecutor(null);

            WebSocketClient webSocketClient = webSocketConnectionStatusExtension.getWebSocketClient();
            if(webSocketClient.isOpen() )
            {
                webSocketClient.close();
            }
            webSocketConnectionStatusExtension.setWebSocketClient(null);

            webSocketConnectionStatusExtension.getLocks().clear();
            webSocketConnectionStatusExtension.getConditions().clear();
            webSocketConnectionStatusExtension.getResponses().clear();

            status.setExtension(null);
        }
    }

    @Override
    public UserContentResponseDefinition call(UserContextRequestDefinition userContextRequest, ConnectionStatusDefinition status) {
        WebSocketConnectionStatusExtensionDefinition webSocketConnectionStatusExtension;

        if (ObjectUtil.isAnyNull(status.getExtension()) || status.getExtension() instanceof WebSocketConnectionStatusExtensionDefinition) {
            throw new StatusRelationshipErrorException();
        } else {
            webSocketConnectionStatusExtension = (WebSocketConnectionStatusExtensionDefinition) status.getExtension();
        }

        WebSocketClient webSocketClient = webSocketConnectionStatusExtension.getWebSocketClient();

        UserContentRequestDefinition userContentRequest = userContextRequest.getContent();
        UUID id = userContentRequest.getID();

        Map<UUID, Lock> locks = webSocketConnectionStatusExtension.getLocks();
        Map<UUID, Condition> conditions = webSocketConnectionStatusExtension.getConditions();

        Lock newLock = new ReentrantLock();
        Condition newCondition = newLock.newCondition();
        locks.put(id, newLock);
        conditions.put(id, newCondition);

        webSocketClient.send(ObjectUtil.transferToString(userContextRequest));

        Map<UUID, UserContentResponseDefinition> responses = webSocketConnectionStatusExtension.getResponses();

        Future<UserContentResponseDefinition> userContentResponseFuture = webSocketConnectionStatusExtension.getExecutor().submit(() -> {
            Lock lock = locks.getOrDefault(id, null);

            UserContentResponseDefinition userContentResponse = new UserContentResponseDefinition();

            if (ObjectUtil.allNotNull(lock)) {
                try {
                    lock.lock();

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

        UserContentResponseDefinition userContentResponse;
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
