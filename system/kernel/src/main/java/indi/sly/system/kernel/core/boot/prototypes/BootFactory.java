package indi.sly.system.kernel.core.boot.prototypes;

import indi.sly.system.kernel.core.boot.prototypes.processors.ABootResolver;
import indi.sly.system.kernel.core.prototypes.AFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootFactory extends AFactory {
    public BootFactory() {
        this.bootResolvers = new CopyOnWriteArrayList<>();
    }

    protected final List<ABootResolver> bootResolvers;

    @Override
    public void init() {

    }
}
