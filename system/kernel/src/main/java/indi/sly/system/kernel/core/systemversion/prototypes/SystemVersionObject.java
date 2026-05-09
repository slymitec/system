package indi.sly.system.kernel.core.systemversion.prototypes;

import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.core.systemversion.values.SystemVersionCacheEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SystemVersionObject extends ACacheableObject<SystemVersionCacheEntity> {
    public String getSystemVersion() {
        return this.cache.getSystemVersion();
    }
}
