package indi.sly.subsystem.periphery.calls.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectionContentObject extends AObject {

}
