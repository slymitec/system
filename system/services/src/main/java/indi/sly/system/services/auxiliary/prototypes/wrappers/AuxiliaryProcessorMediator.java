package indi.sly.system.services.auxiliary.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.wrappers.AMediator;
import indi.sly.system.services.auxiliary.lang.UserContextProcessorCreateFunction;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuxiliaryProcessorMediator extends AMediator {

    public AuxiliaryProcessorMediator() {
        this.creates = new ArrayList<>();
    }

    private final List<UserContextProcessorCreateFunction> creates;

    public List<UserContextProcessorCreateFunction> getCreates() {
        return this.creates;
    }
}
