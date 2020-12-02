package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.core.enviroment.UserSpace;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.processes.definitions.ThreadDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadObject extends ACoreObject {
    public ThreadObject() {
        this.thread = new ThreadDefinition();
    }

    private ThreadDefinition thread;

    public UUID getID() {
        return thread.getID();
    }

    public UUID getProcessID() {
        return thread.getProcessID();
    }

}
