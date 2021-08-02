package indi.sly.system.services.center.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.center.lang.CenterInitializerRunMethodConsumer;
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
        this.runMethods = new ConcurrentHashMap<>();
        this.runTransactions = new ConcurrentHashMap<>();
    }

    private final Map<String, CenterInitializerRunMethodConsumer> runMethods;
    private final Map<String, Long> runTransactions;

    public abstract void start(CenterDefinition center);

    public abstract void finish(CenterDefinition center);

    public final Map<String, CenterInitializerRunMethodConsumer> getRunMethods() {
        return this.runMethods;
    }

    public final Map<String, Long> getRunTransactions() {
        return this.runTransactions;
    }
}
