package indi.sly.system.services.center.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.center.lang.CenterInitializerRunMethodConsumer;
import indi.sly.system.services.center.values.CenterDefinition;
import indi.sly.system.services.center.values.CenterInitializerRunDefinition;
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
        this.runs = new ConcurrentHashMap<>();
    }

    private final Map<String, CenterInitializerRunDefinition> runs;

    protected final void register(String name, CenterInitializerRunMethodConsumer runMethod) {
        this.register(name, runMethod, CenterTransactionType.INDEPENDENCE);
    }

    protected final void register(String name, CenterInitializerRunMethodConsumer runMethod, long runTransaction) {
        if (StringUtil.isNameIllegal(name) || ObjectUtil.isAnyNull(runMethod)) {
            throw new ConditionParametersException();
        }

        CenterInitializerRunDefinition run = new CenterInitializerRunDefinition();
        run.setMethod(runMethod);
        run.setTransaction(runTransaction);

        this.runs.put(name, run);
    }

    public abstract void start(CenterDefinition center);

    public abstract void finish(CenterDefinition center);

    public final boolean containRun(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        return this.runs.containsKey(name);
    }

    public final CenterInitializerRunDefinition getRun(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        CenterInitializerRunDefinition run = this.runs.getOrDefault(name, null);

        if (ObjectUtil.isAnyNull(run)) {
            throw new StatusNotExistedException();
        }

        return run;
    }
}
