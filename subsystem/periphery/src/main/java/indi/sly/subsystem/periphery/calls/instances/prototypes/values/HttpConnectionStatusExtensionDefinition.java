package indi.sly.subsystem.periphery.calls.instances.prototypes.values;

import indi.sly.subsystem.periphery.calls.values.UserContentResponseDefinition;
import org.java_websocket.client.WebSocketClient;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

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
