package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.instances.prototypes.values.WebSocketConnectionStatusHelperDefinition;
import indi.sly.subsystem.periphery.calls.values.*;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebSocketConnectionInitializer extends AConnectionInitializer {
    @Override
    public synchronized void connect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        synchronized (this) {
            WebSocketConnectionStatusHelperDefinition webSocketConnectionStatusHelper = new WebSocketConnectionStatusHelperDefinition();
            status.setHelper(webSocketConnectionStatusHelper);

            webSocketConnectionStatusHelper.setExecutor(Executors.newCachedThreadPool());

            WebSocketClient webSocketClient;

            if (ObjectUtil.allNotNull(status.getHelper())) {
                if (status.getHelper() instanceof WebSocketClient) {
                    webSocketClient = (WebSocketClient) status.getHelper();

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

                webSocketClient = new WebSocketClient(address, new Draft_6455()) {
                    @Override
                    public void onOpen(ServerHandshake handshakeData) {
                    }

                    @Override
                    public void onMessage(String message) {
                        UserContentResponseDefinition userContentResponse = ObjectUtil.transferFromStringOrDefaultProvider(UserContentResponseDefinition.class, message, () -> {
                            throw new StatusUnreadableException();
                        });

                        Map<UUID, UserContentResponseDefinition> responses = status.getResponses();
                        Map<UUID, Lock> locks = webSocketConnectionStatusHelper.getLocks();
                        Map<UUID, Condition> conditions = webSocketConnectionStatusHelper.getConditions();

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
                        status.setRuntime(ConnectStatusRuntimeType.DISCONNECTED);
                        disconnect(connection, status);
                    }

                    @Override
                    public void onError(Exception ex) {
                    }
                };

                status.setHelper(webSocketClient);
            }
        }
    }

    @Override
    public synchronized void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        WebSocketConnectionStatusHelperDefinition webSocketConnectionStatusHelper;

        if (ObjectUtil.isAnyNull(status.getHelper()) || status.getHelper() instanceof WebSocketConnectionStatusHelperDefinition) {
            throw new StatusRelationshipErrorException();
        } else {
            webSocketConnectionStatusHelper = (WebSocketConnectionStatusHelperDefinition) status.getHelper();
        }

        synchronized (this) {
            webSocketConnectionStatusHelper.getExecutor().shutdown();
            webSocketConnectionStatusHelper.setExecutor(null);

            webSocketConnectionStatusHelper.getLocks().clear();
            webSocketConnectionStatusHelper.getConditions().clear();

            status.getResponses().clear();
            status.setHelper(null);
        }
    }

    @Override
    public UserContentResponseDefinition call(UserContextRequestDefinition userContextRequest, ConnectionStatusDefinition status) {
        WebSocketConnectionStatusHelperDefinition webSocketConnectionStatusHelper;

        if (ObjectUtil.isAnyNull(status.getHelper()) || status.getHelper() instanceof WebSocketConnectionStatusHelperDefinition) {
            throw new StatusRelationshipErrorException();
        } else {
            webSocketConnectionStatusHelper = (WebSocketConnectionStatusHelperDefinition) status.getHelper();
        }

        WebSocketClient webSocketClient = webSocketConnectionStatusHelper.getWebSocketClient();

        UserContentRequestDefinition userContentRequest = userContextRequest.getContent();
        UUID id = userContentRequest.getID();

        Map<UUID, Lock> locks = webSocketConnectionStatusHelper.getLocks();
        Map<UUID, Condition> conditions = webSocketConnectionStatusHelper.getConditions();

        Lock newLock = new ReentrantLock();
        Condition newCondition = newLock.newCondition();
        locks.put(id, newLock);
        conditions.put(id, newCondition);

        webSocketClient.send(ObjectUtil.transferToString(userContextRequest));

        Map<UUID, UserContentResponseDefinition> responses = status.getResponses();

        Future<UserContentResponseDefinition> userContentResponseFuture = webSocketConnectionStatusHelper.getExecutor().submit(() -> {
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
                userContentResponse= responses.remove(id);
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
