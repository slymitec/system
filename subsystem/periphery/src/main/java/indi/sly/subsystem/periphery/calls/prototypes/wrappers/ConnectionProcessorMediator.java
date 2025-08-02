package indi.sly.subsystem.periphery.calls.prototypes.wrappers;

import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorConnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorDisconnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorSendFunction;
import indi.sly.subsystem.periphery.core.prototypes.wrappers.AMediator;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectionProcessorMediator extends AMediator {
    public ConnectionProcessorMediator() {
        this.connects = new ArrayList<>();
        this.disconnects = new ArrayList<>();
        this.sends = new ArrayList<>();
   }

    private final List<ConnectionProcessorConnectConsumer> connects;
    private final List<ConnectionProcessorDisconnectConsumer> disconnects;
    private final List<ConnectionProcessorSendFunction> sends;

    public List<ConnectionProcessorConnectConsumer> getConnects() {
        return this.connects;
    }

    public List<ConnectionProcessorDisconnectConsumer> getDisconnects() {
        return this.disconnects;
    }

    public List<ConnectionProcessorSendFunction> getSends() {
        return this.sends;
    }
}
