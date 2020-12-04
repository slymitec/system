package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DumpObject extends ACorePrototype {
    //Store to memory
    //Read Info...
}
