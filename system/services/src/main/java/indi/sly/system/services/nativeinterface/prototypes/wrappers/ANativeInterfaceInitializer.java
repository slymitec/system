package indi.sly.system.services.nativeinterface.prototypes.wrappers;

import indi.sly.system.services.nativeinterface.lang.RunSelfConsumer;
import indi.sly.system.services.nativeinterface.prototypes.NativeInterfaceContentObject;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ANativeInterfaceInitializer {
    public abstract void start(NativeInterfaceDefinition nativeInterface);

    public abstract void finish(NativeInterfaceDefinition nativeInterface);

    public abstract void run(String name, RunSelfConsumer run, NativeInterfaceContentObject content);
}
