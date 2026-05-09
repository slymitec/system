package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.memory.repositories.prototypes.ACacheRepositoryObject;
import indi.sly.system.kernel.processes.values.ProcessChildCacheEntity;
import indi.sly.system.kernel.processes.values.ProcessInfoEntryCacheEntity;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoEntryRepositoryObject extends ACacheRepositoryObject<ProcessInfoEntryCacheEntity> {
}
