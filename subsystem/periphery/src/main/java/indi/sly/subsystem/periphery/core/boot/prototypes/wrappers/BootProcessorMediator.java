package indi.sly.subsystem.periphery.core.boot.prototypes.wrappers;

import indi.sly.subsystem.periphery.core.boot.lang.BootStartConsumer;
import indi.sly.subsystem.periphery.core.prototypes.wrappers.AMediator;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

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
