package indi.sly.system.services.center.prototypes.wrappers;

import indi.sly.system.services.center.lang.RunSelfConsumer;
import indi.sly.system.services.center.prototypes.CenterContentObject;
import indi.sly.system.services.center.values.CenterDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ACenterInitializer {
    public abstract void start(CenterDefinition center);

    public abstract void finish(CenterDefinition center);

    public abstract void run(String name, RunSelfConsumer run, CenterContentObject content);
}
