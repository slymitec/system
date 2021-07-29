package indi.sly.system.services.core;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional
public class LoaderService extends AService {
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
