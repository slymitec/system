package indi.sly.system.kernel.core.boot.prototypes.wrappers;

import indi.sly.system.kernel.core.boot.lang.BootStartConsumer;
import indi.sly.system.kernel.core.prototypes.wrappers.AMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootProcessorMediator extends AMediator {
    public BootProcessorMediator() {
        this.starts = new ArrayList<>();
    }

    private final List<BootStartConsumer> starts;

    public List<BootStartConsumer> getStarts() {
        return this.starts;
    }
}
