package indi.sly.system.services.center.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.kernel.processes.values.ThreadRunDefinition;
import indi.sly.system.services.center.lang.FinishConsumer;
import indi.sly.system.services.center.lang.GetContentFunction;
import indi.sly.system.services.center.lang.RunConsumer;
import indi.sly.system.services.center.lang.StartFunction;
import indi.sly.system.services.center.prototypes.wrappers.CenterProcessorMediator;
import indi.sly.system.services.center.values.CenterDefinition;
import indi.sly.system.services.center.values.CenterStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CenterObject extends AValueProcessPrototype<CenterDefinition> {
    protected CenterProcessorMediator processorMediator;
    protected CenterStatusDefinition status;

    public void start() {
        CenterDefinition center = this.getSelf();

        List<StartFunction> resolvers = this.processorMediator.getStarts();

        for (StartFunction resolver : resolvers) {
            resolver.accept(center, this.status);
        }
    }

    public void finish() {
        CenterDefinition center = this.getSelf();

        List<FinishConsumer> resolvers = this.processorMediator.getFinishes();

        for (FinishConsumer resolver : resolvers) {
            resolver.accept(center, this.status);
        }
    }

    private synchronized CenterDefinition getSelf() {
        this.init();

        return this.value;
    }

    public UUID getID() {
        this.init();

        return this.value.getID();
    }

    public long getRuntime() {
        return this.status.getRuntime();
    }

    public synchronized void run(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        CenterDefinition center = this.getSelf();
        CenterContentObject content = this.getContent();

        List<RunConsumer> resolvers = this.processorMediator.getRuns();

        for (RunConsumer resolver : resolvers) {
            resolver.accept(center, this.status, name, this::run, content);
        }
    }

    public synchronized CenterContentObject getContent() {
        CenterDefinition center = this.getSelf();

        ThreadContextObject threadContext = null;

        List<GetContentFunction> resolvers = this.processorMediator.getContents();

        for (GetContentFunction resolver : resolvers) {
            threadContext = resolver.apply(center, this.status, threadContext);
        }

        CenterContentObject centerContent = this.factoryManager.create(CenterContentObject.class);
        centerContent.threadContext = threadContext;

        return centerContent;
    }
}
