package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.instances.prototypes.values.HttpConnectionStatusExtensionDefinition;
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

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HttpConnectionInitializer extends AConnectionInitializer {
    @Override
    public synchronized void connect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        synchronized (this) {
            HttpConnectionStatusExtensionDefinition httpConnectionStatusExtension = new HttpConnectionStatusExtensionDefinition();

            RestClient restClient = RestClient.builder()
                    .requestFactory(new HttpComponentsClientHttpRequestFactory())
                    .baseUrl(connection.getAddress())
                    .build();

            httpConnectionStatusExtension.setRestClient(restClient);

            status.setExtension(httpConnectionStatusExtension);
        }
    }

    @Override
    public synchronized void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
        HttpConnectionStatusExtensionDefinition httpConnectionStatusExtension;

        if (ObjectUtil.isAnyNull(status.getExtension()) || status.getExtension() instanceof HttpConnectionStatusExtensionDefinition) {
            throw new StatusRelationshipErrorException();
        } else {
            httpConnectionStatusExtension = (HttpConnectionStatusExtensionDefinition) status.getExtension();
        }

        synchronized (this) {
            httpConnectionStatusExtension.setRestClient(null);
            status.setExtension(null);
        }
    }

    @Override
    public UserContentResponseDefinition call(UserContextRequestDefinition userContextRequest, ConnectionStatusDefinition status) {
        HttpConnectionStatusExtensionDefinition httpConnectionStatusExtension;

        if (ObjectUtil.isAnyNull(status.getExtension()) || status.getExtension() instanceof HttpConnectionStatusExtensionDefinition) {
            throw new StatusRelationshipErrorException();
        } else {
            httpConnectionStatusExtension = (HttpConnectionStatusExtensionDefinition) status.getExtension();
        }

        RestClient systemRestClient = httpConnectionStatusExtension.getRestClient();

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
