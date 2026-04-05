package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.supports.ObjectUtil;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AIndependentBytesValueProcessObject<T> extends ABytesValueProcessObject<T, AObject> {
}
