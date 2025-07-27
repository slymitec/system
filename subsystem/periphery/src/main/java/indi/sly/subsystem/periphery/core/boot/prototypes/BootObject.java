package indi.sly.subsystem.periphery.core.boot.prototypes;

import indi.sly.subsystem.periphery.core.boot.lang.BootStartConsumer;
import indi.sly.subsystem.periphery.core.boot.prototypes.wrappers.BootProcessorMediator;
import indi.sly.subsystem.periphery.core.boot.values.StartupType;
import indi.sly.subsystem.periphery.core.prototypes.AObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootObject extends AObject implements IStartupCapable {
    public BootObject() {
        this.startupStatus = StartupType.SHUTDOWN;
    }

    protected BootProcessorMediator processorMediator;
    protected long startupStatus;

    @Override
    public void startup(long startup) {
        for (BootStartConsumer resolver : this.processorMediator.getStarts()) {
            resolver.accept(startup);
        }
        this.startupStatus = startup;
    }

    @Override
    public void shutdown() {
    }

    public long getStartupStatus() {
        return this.startupStatus;
    }
}
