package indi.sly.system.services.center.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.center.lang.CenterProcessorFinishConsumer;
import indi.sly.system.services.center.lang.CenterProcessorStartFunction;
import indi.sly.system.services.center.prototypes.wrappers.CenterProcessorMediator;
import indi.sly.system.services.center.values.CenterDefinition;
import indi.sly.system.services.center.values.CenterStatusRuntimeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SetRuntimeResolver extends APrototype implements ICenterResolver {
    public SetRuntimeResolver() {
        this.start = (center, status) -> {
            status.setRuntime(CenterStatusRuntimeType.RUNNING);
        };

        this.finish = (center, status) -> {
            status.setRuntime(CenterStatusRuntimeType.FINISHED);
        };
    }

    @Override
    public int order() {
        return 3;
    }

    private final CenterProcessorStartFunction start;
    private final CenterProcessorFinishConsumer finish;

    @Override
    public void resolve(CenterDefinition center, CenterProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
    }
}
