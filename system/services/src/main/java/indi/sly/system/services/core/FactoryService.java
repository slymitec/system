package indi.sly.system.services.core;

import indi.sly.system.kernel.core.AService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FactoryService extends AService {
    @Override
    public void startup(long startup) {
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    public void boot() {

    }
}
