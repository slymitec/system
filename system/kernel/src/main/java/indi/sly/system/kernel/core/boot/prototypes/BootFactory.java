package indi.sly.system.kernel.core.boot.prototypes;

import indi.sly.system.kernel.core.boot.prototypes.processors.*;
import indi.sly.system.kernel.core.boot.prototypes.mediators.BootProcessorMediator;
import indi.sly.system.kernel.core.boot.values.BootDefinition;
import indi.sly.system.kernel.core.boot.values.StartupType;
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
        this.bootResolvers.add(this.coreManager.create(BootObjectsResolver.class));
        this.bootResolvers.add(this.coreManager.create(BootProcessesLaterResolver.class));
        this.bootResolvers.add(this.coreManager.create(BootProcessesResolver.class));
        this.bootResolvers.add(this.coreManager.create(BootUserResolver.class));

        Collections.sort(this.bootResolvers);
    }

    private BootObject createBootObject(BootProcessorMediator processorMediator, BootDefinition definition) {
        BootObject boot = this.coreManager.create(BootObject.class);

        boot.processorMediator = processorMediator;
        boot.setDefinition(definition);

        return boot;
    }

    public BootObject buildBoot() {
        BootProcessorMediator processorMediator = this.coreManager.create(BootProcessorMediator.class);
        for (ABootResolver bootResolver : this.bootResolvers) {
            bootResolver.resolve(processorMediator);
        }

        BootDefinition boot = new BootDefinition();

        boot.setStartupStatus(StartupType.SHUTDOWN);

        return this.createBootObject(processorMediator,boot);
    }

}
