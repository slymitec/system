package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.memory.repositories.prototypes.ACacheRepositoryObject;
import indi.sly.system.kernel.processes.values.ProcessCacheEntity;
import indi.sly.system.kernel.processes.values.ProcessChildCacheEntity;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessChildRepositoryObject extends ACacheRepositoryObject<ProcessChildCacheEntity> {
}
