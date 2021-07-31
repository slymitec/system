package indi.sly.system.services.nativeinterface;

import indi.sly.system.kernel.core.AService;
import indi.sly.system.services.nativeinterface.prototypes.NativeInterfaceFactory;
import indi.sly.system.services.nativeinterface.prototypes.NativeInterfaceObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional
public class NativeInterfaceSerice extends AService {
    protected NativeInterfaceFactory factory;

    public NativeInterfaceObject get(String name) {
        return null;
    }
}
