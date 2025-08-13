package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.values.*;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.ObjectUtil;
import jakarta.inject.Named;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HttpConnectionInitializer extends AConnectionInitializer {
    @Override
    public synchronized void connect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        synchronized (this) {
            status.setExecutor(Executors.newCachedThreadPool());

            RestClient restClient = RestClient.builder()
                    .requestFactory(new HttpComponentsClientHttpRequestFactory())
                    .baseUrl(connection.getAddress())
                    .build();

        }
    }

    @Override
    public synchronized void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        synchronized (this) {
            status.getLocks().clear();
            status.getConditions().clear();
            status.getResponses().clear();
            status.setHelper(null);
        }
    }

    @Override
    public void send(UserContextRequestDefinition userContextRequest, ConnectionStatusDefinition status) {
    }

    @Override
    public UserContentResponseDefinition receive(UserContextRequestDefinition userContextRequest, ConnectionStatusDefinition status) {
        return null;
    }
}
