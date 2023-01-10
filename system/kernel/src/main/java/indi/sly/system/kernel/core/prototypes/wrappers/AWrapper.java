package indi.sly.system.kernel.core.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.APrototype;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AWrapper extends APrototype {
}
