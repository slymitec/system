package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.values.*;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
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

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebSocketConnectionInitializer extends AConnectionInitializer {
    @Override
    public void connect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        WebSocketClient systemWebSocketClient;

        if (ObjectUtil.allNotNull(status.getHelper())) {
            if (status.getHelper() instanceof WebSocketClient) {
                systemWebSocketClient = (WebSocketClient) status.getHelper();

                systemWebSocketClient.reconnect();
            } else {
                throw new StatusRelationshipErrorException();
            }
        } else {
            URI address = null;
            try {
                address = new URI(connection.getAddress());
            } catch (URISyntaxException e) {
                throw new ConditionParametersException();
            }

            systemWebSocketClient = new WebSocketClient(address, new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                }

                @Override
                public void onMessage(String message) {
                    UserContentResponseDefinition userContentResponse = ObjectUtil.transferFromStringOrDefaultProvider(UserContentResponseDefinition.class, message, () -> {
                        throw new StatusUnreadableException();
                    });

                    Map<UUID, UserContentRequestDefinition> requests = status.getRequest();
                    Map<UUID, UserContentResponseDefinition> responses = status.getResponses();

                    UUID id = userContentResponse.getID();

                    UserContentRequestDefinition userContentRequest;

                    userContentRequest = requests.getOrDefault(id, null);
                    if (ObjectUtil.isAnyNull(userContentRequest)) {
                        return;
                    } else {
                        requests.remove(id);
                    }

                    synchronized (userContentRequest) {
                        if (!ValueUtil.isAnyNullOrEmpty(userContentRequest.getID())) {
                            responses.put(id, userContentResponse);
                            userContentRequest.setID(UUIDUtil.getEmpty());
                        }
                        userContentRequest.notify();
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

            status.setHelper(systemWebSocketClient);
        }
    }

    @Override
    public void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        status.getRequest().clear();
        status.getResponses().clear();
        status.setHelper(null);
    }

    @Override
    public void send(UserContextRequestDefinition userContextRequest, ConnectionStatusDefinition status) {
        WebSocketClient systemWebSocketClient;

        if (ObjectUtil.isAnyNull(status.getHelper()) || status.getHelper() instanceof WebSocketClient) {
            throw new StatusRelationshipErrorException();
        } else {
            systemWebSocketClient = (WebSocketClient) status.getHelper();
        }

        systemWebSocketClient.send(ObjectUtil.transferToString(userContextRequest));
    }
}
