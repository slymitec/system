package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.kernel.memory.repositories.prototypes.ACacheRepositoryObject;
import indi.sly.system.kernel.security.values.GroupCacheEntity;
import indi.sly.system.kernel.security.values.SecurityDescriptorCacheEntity;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityDescriptorCacheRepositoryObject extends ACacheRepositoryObject<SecurityDescriptorCacheEntity> {
}
