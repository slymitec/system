package indi.sly.system.kernel.core.boot.prototypes;

import indi.sly.system.kernel.core.boot.prototypes.processors.*;
import indi.sly.system.kernel.core.boot.prototypes.wrappers.BootProcessorMediator;
import indi.sly.system.kernel.core.prototypes.AFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
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
        this.bootResolvers.add(this.factoryManager.create(BootObjectsResolver.class));
        this.bootResolvers.add(this.factoryManager.create(BootProcessesLaterResolver.class));
        this.bootResolvers.add(this.factoryManager.create(BootProcessesResolver.class));
        this.bootResolvers.add(this.factoryManager.create(BootUserResolver.class));

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
