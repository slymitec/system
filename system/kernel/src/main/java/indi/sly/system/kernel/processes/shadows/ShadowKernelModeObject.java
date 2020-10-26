package indi.sly.system.kernel.processes.shadows;

import indi.sly.system.kernel.core.ACoreObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ShadowKernelModeObject extends ACoreObject {
    public void shadow() {

    }

    public void exit() {

    }
}
