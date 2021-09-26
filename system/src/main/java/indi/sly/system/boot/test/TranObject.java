package indi.sly.system.boot.test;

import indi.sly.system.common.lang.Provider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TranObject {
    @Transactional
    public <T> T doo(Provider<T> p) {
        return p.acquire();
    }
}
