package indi.sly.system.services.center.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.center.lang.FinishConsumer;
import indi.sly.system.services.center.lang.RunConsumer;
import indi.sly.system.services.center.lang.StartFunction;
import indi.sly.system.services.center.prototypes.wrappers.ACenterInitializer;
import indi.sly.system.services.center.prototypes.wrappers.CenterProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InitializerResolver extends APrototype implements ICenterResolver {
    public InitializerResolver() {
        this.start = (center, status) -> {
            ACenterInitializer initializer = center.getInitializer();

            initializer.start(center);
        };

        this.finish = (center, status) -> {
            ACenterInitializer initializer = center.getInitializer();

            initializer.finish(center);
        };

        this.run = (center, status, name, run, content) -> {
            ACenterInitializer initializer = center.getInitializer();

            initializer.run(name, run, content);
        };
    }

    @Override
    public int order() {
        return 2;
    }

    private final StartFunction start;
    private final FinishConsumer finish;
    private final RunConsumer run;

    @Override
    public void resolve(CenterProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
        processorMediator.getRuns().add(this.run);
    }
}
