package indi.sly.subsystem.periphery.proxies.prototypes.mediators;

import indi.sly.subsystem.periphery.core.prototypes.wrappers.AMediator;
import indi.sly.subsystem.periphery.proxies.lang.ProxyInvokeFunction;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;
import java.util.Set;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProxyProcessorMediator extends AMediator {
    public ProxyProcessorMediator() {
        this.invokes = new HashSet<>();
    }

    private final Set<ProxyInvokeFunction> invokes;

    public Set<ProxyInvokeFunction> getInvokes() {
        return this.invokes;
    }
}
