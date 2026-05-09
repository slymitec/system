package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.kernel.memory.repositories.prototypes.ACacheRepositoryObject;
import indi.sly.system.kernel.objects.values.DumpCacheEntity;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DumpCacheRepositoryObject extends ACacheRepositoryObject<DumpCacheEntity> {
}
