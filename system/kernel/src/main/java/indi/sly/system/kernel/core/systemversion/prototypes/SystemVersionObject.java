package indi.sly.system.kernel.core.systemversion.prototypes;

import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.core.systemversion.values.SystemVersionCacheEntity;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SystemVersionObject extends ACacheableObject<SystemVersionCacheEntity> {
    public String getSystemVersion() {
        String systemVersion = this.cache.getSystemVersion();

        if (ValueUtil.isNullOrEmpty(systemVersion)) {
            systemVersion = StringUtil.EMPTY;
        }

        return systemVersion;
    }
}
