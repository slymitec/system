package indi.sly.system.services.jobs.prototypes.mediators;

import indi.sly.system.kernel.core.prototypes.AMediator;
import indi.sly.system.services.jobs.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.jobs.lang.UserContextProcessorFinishFunction;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextProcessorMediator extends AMediator {

    public UserContextProcessorMediator() {
        this.creates = new ArrayList<>();
        this.finishes = new ArrayList<>();
    }

    private final List<UserContextProcessorCreateFunction> creates;
    private final List<UserContextProcessorFinishFunction> finishes;

    public List<UserContextProcessorCreateFunction> getCreates() {
        return this.creates;
    }

    public List<UserContextProcessorFinishFunction> getFinishes() {
        return this.finishes;
    }
}
