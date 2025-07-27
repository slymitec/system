package indi.sly.clisubsystem.periphery.core.boot.prototypes;

import indi.sly.clisubsystem.periphery.core.boot.prototypes.processors.ABootResolver;
import indi.sly.clisubsystem.periphery.core.boot.prototypes.wrappers.BootProcessorMediator;
import indi.sly.clisubsystem.periphery.core.prototypes.AFactory;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootFactory extends AFactory {
    public BootFactory() {
        this.bootResolvers = new CopyOnWriteArrayList<>();
    }

    protected final List<ABootResolver> bootResolvers;

    @Override
    public void init() {
        Collections.sort(this.bootResolvers);
    }

    public BootObject buildBoot() {
        BootProcessorMediator processorMediator = this.factoryManager.create(BootProcessorMediator.class);
        for (ABootResolver bootResolver : this.bootResolvers) {
            bootResolver.resolve(processorMediator);
        }

        BootObject boot = this.factoryManager.create(BootObject.class);

        boot.processorMediator = processorMediator;

        return boot;
    }

}
