package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.services.core.environment.values.ServiceKernelSpaceExtensionDefinition;
import indi.sly.system.services.job.prototypes.processors.AJobInitializer;
import indi.sly.system.services.job.values.JobAttributeType;
import indi.sly.system.services.job.values.JobDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobBuilder extends ABuilder {
    protected JobFactory factory;

    public JobObject create(String name, long attribute, UUID processID, AJobInitializer initializer) {
        if (StringUtil.isNameIllegal(name) || ObjectUtil.isAnyNull(initializer)) {
            throw new ConditionParametersException();
        }

        ServiceKernelSpaceExtensionDefinition serviceSpace = (ServiceKernelSpaceExtensionDefinition) this.factoryManager.getKernelSpace().getServiceSpace();

        if (serviceSpace.getNamedJobIDs().containsKey(name)) {
            throw new StatusAlreadyExistedException();
        }

        JobDefinition job = new JobDefinition();

        job.setID(UUIDUtil.createRandom());
        job.setAttribute(attribute);
        job.setName(name);
        if (LogicalUtil.isAllExist(job.getAttribute(), JobAttributeType.HAS_PROCESS)
                && !ValueUtil.isAnyNullOrEmpty(processID)) {
            job.setProcessID(processID);
        }
        job.setInitializer(initializer);

        serviceSpace.getJobs().put(job.getID(), job);
        serviceSpace.getNamedJobIDs().put(job.getName(), job.getID());

        return this.factory.build(job);
    }

    public void delete(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ServiceKernelSpaceExtensionDefinition serviceSpace = (ServiceKernelSpaceExtensionDefinition) this.factoryManager.getKernelSpace().getServiceSpace();

        if (!serviceSpace.getJobs().containsKey(id)) {
            throw new StatusNotExistedException();
        }

        JobDefinition job = serviceSpace.getJobs().getOrDefault(id, null);

        if (ObjectUtil.isAnyNull(job)) {
            throw new StatusNotExistedException();
        }

        serviceSpace.getJobs().remove(job.getID());
        serviceSpace.getNamedJobIDs().remove(job.getName());
    }
}
