package indi.sly.system.services.center.instances.prototypes.processors;

import indi.sly.system.services.center.lang.CenterObjectRunConsumer;
import indi.sly.system.services.center.prototypes.CenterContentObject;
import indi.sly.system.services.center.prototypes.processors.ACenterInitializer;
import indi.sly.system.services.center.values.CenterDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RuntimeCenterInitializer extends ACenterInitializer {
    public RuntimeCenterInitializer() {

    }

    @Override
    public void start(CenterDefinition center) {
    }

    @Override
    public void finish(CenterDefinition center) {
    }

    private void createSession(CenterObjectRunConsumer run, CenterContentObject content) {

    }

}
