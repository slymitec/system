package indi.sly.subsystem.periphery.proxies.prototypes.mediators;

import indi.sly.subsystem.periphery.core.prototypes.wrappers.AMediator;
import indi.sly.subsystem.periphery.proxies.lang.*;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemoteProcessorMediator extends AMediator {
    public RemoteProcessorMediator() {
        this.invokes = new CopyOnWriteArrayList<>();
        this.isExpires = new CopyOnWriteArrayList<>();
        this.expires = new CopyOnWriteArrayList<>();
        this.dies = new CopyOnWriteArrayList<>();
    }

    private final List<RemoteProcessorInvokeFunction> invokes;
    private final List<RemoteProcessorIsExpiredFunction> isExpires;
    private final List<RemoteProcessorExpireConsumer> expires;
    private final List<RemoteProcessorDieConsumer> dies;

    public List<RemoteProcessorInvokeFunction> getInvokes() {
        return this.invokes;
    }

    public List<RemoteProcessorExpireConsumer> getExpires() {
        return this.expires;
    }

    public List<RemoteProcessorIsExpiredFunction> getIsExpires() {
        return this.isExpires;
    }

    public List<RemoteProcessorDieConsumer> getDies() {
        return this.dies;
    }
}
