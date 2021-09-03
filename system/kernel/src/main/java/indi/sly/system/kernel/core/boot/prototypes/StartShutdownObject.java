package indi.sly.system.kernel.core.boot.prototypes;

import indi.sly.system.kernel.core.prototypes.AObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StartShutdownObject extends AObject {

}
