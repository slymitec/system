package indi.sly.subsystem.periphery.proxies.prototypes.processors;

import indi.sly.subsystem.periphery.core.prototypes.processors.AResolver;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorDieConsumer;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorExpireConsumer;
import indi.sly.subsystem.periphery.proxies.lang.RemoteProcessorInvokeFunction;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemoteDieResolver extends AResolver implements IRemoteResolver {
    private final RemoteProcessorDieConsumer die;

    public RemoteDieResolver() {
        this.die = (remote) -> {
            remote.setTaskName(null);
        };
    }

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void resolve(RemoteDefinition remote, RemoteProcessorMediator processorMediator) {
        if (ValueUtil.isAnyNullOrEmpty(remote.getValue())) {
            processorMediator.getDies().add(this.die);
        }
    }
}
