package indi.sly.system.kernel.processes.prototypes.wrappers;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.common.lang.Function1;
import indi.sly.system.common.lang.Function2;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreaterProcessorMediator extends APrototype {
    public ProcessCreaterProcessorMediator() {
        this.readProcessStatuses = new ArrayList<>();

    }

    private final List<Function2<Long, Long, ProcessEntity>> readProcessStatuses;

}
