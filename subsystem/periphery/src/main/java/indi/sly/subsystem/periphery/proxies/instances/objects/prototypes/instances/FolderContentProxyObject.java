package indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.instances;

import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.AInfoContentProxyObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FolderContentProxyObject extends AInfoContentProxyObject {
}
