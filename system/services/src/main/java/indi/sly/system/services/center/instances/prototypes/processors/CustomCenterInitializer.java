package indi.sly.system.services.center.instances.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.services.center.lang.CenterRunConsumer;
import indi.sly.system.services.center.prototypes.CenterContentObject;
import indi.sly.system.services.center.prototypes.processors.ACenterInitializer;
import indi.sly.system.services.center.values.CenterDefinition;
import indi.sly.system.services.center.values.CenterTransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomCenterInitializer extends ACenterInitializer {
    public CustomCenterInitializer() {
        this.register("consumer", this::consumer, CenterTransactionType.INDEPENDENCE);
    }

    @Override
    public void start(CenterDefinition center) {
    }

    @Override
    public void finish(CenterDefinition center) {
    }

    private void consumer(CenterRunConsumer run, CenterContentObject content) {
        Consumer resolver = content.getDatumOrDefault(Consumer.class, "method", null);

        if (ObjectUtil.isAnyNull(resolver)) {
            throw new ConditionParametersException();
        }

        resolver.accept();
    }
}
