package indi.sly.system.services.center.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.center.lang.InitializerConsumer;
import indi.sly.system.services.center.values.CenterDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ACenterInitializer extends APrototype {
    public ACenterInitializer() {
        this.runs = new ConcurrentHashMap<>();
    }

    private final Map<String, InitializerConsumer> runs;

    public abstract void start(CenterDefinition center);

    public abstract void finish(CenterDefinition center);

    public final Map<String, InitializerConsumer> getRuns() {
        return this.runs;
    }
}
