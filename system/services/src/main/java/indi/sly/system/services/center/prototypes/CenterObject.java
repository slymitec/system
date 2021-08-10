package indi.sly.system.services.center.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.services.center.lang.CenterProcessorFinishConsumer;
import indi.sly.system.services.center.lang.CenterProcessorContentFunction;
import indi.sly.system.services.center.lang.CenterProcessorRunConsumer;
import indi.sly.system.services.center.lang.CenterProcessorStartFunction;
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

    public UUID getID() {
        this.init();

        return this.value.getID();
    }

    public long getRuntime() {
        return this.status.getRuntime();
    }

    private synchronized CenterDefinition getSelf() {
        this.init();

        return this.value;
    }

    public void start() {
        CenterDefinition center = this.getSelf();

        List<CenterProcessorStartFunction> resolvers = this.processorMediator.getStarts();

        for (CenterProcessorStartFunction resolver : resolvers) {
            resolver.accept(center, this.status);
        }
    }

    public void finish() {
        CenterDefinition center = this.getSelf();

        List<CenterProcessorFinishConsumer> resolvers = this.processorMediator.getFinishes();

        for (CenterProcessorFinishConsumer resolver : resolvers) {
            resolver.accept(center, this.status);
        }
    }

    public synchronized void run(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        CenterDefinition center = this.getSelf();
        CenterContentObject content = this.getContent();

        List<CenterProcessorRunConsumer> resolvers = this.processorMediator.getRuns();

        for (CenterProcessorRunConsumer resolver : resolvers) {
            resolver.accept(center, this.status, name, this::run, content);
        }
    }

    public synchronized CenterContentObject getContent() {
        CenterDefinition center = this.getSelf();

        ThreadContextObject threadContext = null;

        List<CenterProcessorContentFunction> resolvers = this.processorMediator.getContents();

        for (CenterProcessorContentFunction resolver : resolvers) {
            threadContext = resolver.apply(center, this.status, threadContext);
        }

        CenterContentObject centerContent = this.factoryManager.create(CenterContentObject.class);
        centerContent.threadContext = threadContext;

        return centerContent;
    }
}
