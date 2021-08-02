package indi.sly.system.services.center.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.center.lang.CenterInitializerRunMethodConsumer;
import indi.sly.system.services.center.values.CenterDefinition;
import indi.sly.system.services.center.values.CenterTransactionType;
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

    protected final void register(String name, CenterInitializerRunMethodConsumer runMethod) {
        if (StringUtil.isNameIllegal(name) || ObjectUtil.isAnyNull(runMethod)) {
            throw new ConditionParametersException();
        }

        this.runMethods.put(name, runMethod);
    }

    protected final void register(String name, CenterInitializerRunMethodConsumer runMethod, long transaction) {
        this.register(name, runMethod);

        this.runTransactions.put(name, transaction);
    }

    public abstract void start(CenterDefinition center);

    public abstract void finish(CenterDefinition center);

    public final CenterInitializerRunMethodConsumer getRunMethodOrNull(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        return this.runMethods.getOrDefault(name, null);
    }

    public final long getRunTransactionOrDefault(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        return this.runTransactions.getOrDefault(name, CenterTransactionType.INDEPENDENCE);
    }
}
