package indi.sly.system.services.nativeinterface.prototypes;

import indi.sly.system.kernel.core.prototypes.APrototype;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NativeInterfaceRepositoryObject extends APrototype {

}
