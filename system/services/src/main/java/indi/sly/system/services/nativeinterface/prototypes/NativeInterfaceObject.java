package indi.sly.system.services.nativeinterface.prototypes;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.nativeinterface.prototypes.wrappers.NativeInterfaceProcessorMediator;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceDefinition;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NativeInterfaceObject extends APrototype {
    protected NativeInterfaceFactory factory;
    protected NativeInterfaceProcessorMediator processorMediator;

    protected UUID id;
    protected NativeInterfaceStatusDefinition status;

    public void init() {

    }

    public void end() {

    }

    private synchronized NativeInterfaceDefinition getSelf() {
        return null;
    }

    public synchronized NativeInterfaceMethodObject getMethod(String name) {
        return null;
    }
}
