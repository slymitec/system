package indi.sly.system.kernel.core.enviroment.values;

import indi.sly.system.common.values.ADefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AKernelSpaceExtensionDefinition<T> extends ADefinition<T> {

}
