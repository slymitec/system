package indi.sly.subsystem.periphery.calls.instances.prototypes.values;

import org.springframework.web.client.RestClient;

public class HttpConnectionStatusExtensionDefinition extends AConnectionStatusExtensionDefinition<HttpConnectionStatusExtensionDefinition> {
    public HttpConnectionStatusExtensionDefinition() {
    }

    private RestClient restClient;

    public RestClient getRestClient() {
        return restClient;
    }

    public void setRestClient(RestClient restClient) {
        this.restClient = restClient;
    }
}
