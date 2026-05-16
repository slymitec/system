package indi.sly.system.kernel.core.boot.prototypes;

import indi.sly.system.kernel.core.boot.lang.BootStartConsumer;
import indi.sly.system.kernel.core.boot.prototypes.mediators.BootProcessorMediator;
import indi.sly.system.kernel.core.boot.values.BootDefinition;
import indi.sly.system.kernel.core.prototypes.ADefinitionObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootObject extends ADefinitionObject<BootDefinition> implements IStartupCapable {
    protected BootProcessorMediator processorMediator;

    @Override
    public void startup(long startup) {
        for (BootStartConsumer resolver : this.processorMediator.getStarts()) {
            resolver.accept(startup);
        }

        this.definition.setStartupStatus(startup);
    }

    @Override
    public void shutdown() {
    }

    public long getStartupStatus() {
        return this.definition.getStartupStatus();
    }
}
