package indi.sly.system.services.nativeinterface;

import indi.sly.system.kernel.core.AService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional
public class NativeInterfaceSerice extends AService {
}
