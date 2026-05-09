package indi.sly.system.kernel.security.prototypes;

import indi.sly.system.kernel.memory.repositories.prototypes.ACacheRepositoryObject;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;
import indi.sly.system.kernel.security.values.AccountCacheEntity;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountCacheRepositoryObject extends ACacheRepositoryObject<AccountCacheEntity> {
}
