package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.kernel.core.systemversion.values.SystemVersionCacheEntity;
import indi.sly.system.kernel.core.values.NoneCacheEntity;
import indi.sly.system.kernel.memory.repositories.prototypes.ACacheRepositoryObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NoneCacheRepositoryObject extends ACacheRepositoryObject<NoneCacheEntity> {
}
