package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.values.*;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.ObjectUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HttpConnectionInitializer extends AConnectionInitializer {
    @Override
    public synchronized void connect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        synchronized (this) {
            RestClient restClient = RestClient.builder()
                    .requestFactory(new HttpComponentsClientHttpRequestFactory())
                    .baseUrl(connection.getAddress())
                    .build();

            status.getResponses().clear();
            status.setHelper(restClient);
        }
    }

    @Override
    public synchronized void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        synchronized (this) {
            status.getResponses().clear();
            status.setHelper(null);
        }
    }

    @Override
    public UserContentResponseDefinition call(UserContextRequestDefinition userContextRequest, ConnectionStatusDefinition status) {
        RestClient systemRestClient;

        if (ObjectUtil.isAnyNull(status.getHelper()) || status.getHelper() instanceof RestClient) {
            throw new StatusRelationshipErrorException();
        } else {
            systemRestClient = (RestClient) status.getHelper();
        }

        Map<UUID, UserContentResponseDefinition> responses = status.getResponses();

        UserContentRequestDefinition userContentRequest = userContextRequest.getContent();

        UserContentResponseDefinition userContentResponse = systemRestClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userContentRequest)
                .retrieve()
                .body(UserContentResponseDefinition.class);

        if (ObjectUtil.isAnyNull(userContentResponse)) {
            throw new StatusUnexpectedException();
        }

        return userContentResponse;
    }
}
