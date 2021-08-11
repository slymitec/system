package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.processes.values.ProcessTokenLimitType;
import indi.sly.system.services.job.values.JobPointerDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobPointerBuilder extends APrototype {
    protected JobFactory factory;

    public void create(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        KernelConfigurationDefinition configuration = this.factoryManager.getKernelSpace().getConfiguration();

        Map<UUID, JobPointerDefinition> jobPointers = this.factory.getJobPointers();

        if (jobPointers.containsKey(id)) {
            throw new StatusAlreadyExistedException();
        }

        JobPointerDefinition jobPointer = new JobPointerDefinition();

        jobPointer.setJobID(id);
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();
        int limit = processToken.getLimits().getOrDefault(ProcessTokenLimitType.JOB_PROTOTYPE_CACHES_MAX,
                configuration.PROCESSES_TOKEN_DEFAULT_LIMIT.get(ProcessTokenLimitType.JOB_PROTOTYPE_CACHES_MAX));
        jobPointer.setLimit(limit);
        DateTimeObject dateTime = this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();
        jobPointer.getDate().put(DateTimeType.CREATE, nowDateTime);
        jobPointer.getDate().put(DateTimeType.ACCESS, nowDateTime);

        jobPointers.put(id, jobPointer);
    }
}
