package indi.sly.system.services.center;

import indi.sly.system.kernel.core.AService;
import indi.sly.system.kernel.core.prototypes.CoreRepositoryObject;
import indi.sly.system.services.center.prototypes.CenterFactory;
import indi.sly.system.services.center.prototypes.CenterObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional
public class CenterService extends AService {
    protected CenterFactory factory;

    public CenterObject get(String name) {
        CoreRepositoryObject coreRepository = this.factoryManager.getCoreRepository();



        return this.factory.build(null);
    }
}
