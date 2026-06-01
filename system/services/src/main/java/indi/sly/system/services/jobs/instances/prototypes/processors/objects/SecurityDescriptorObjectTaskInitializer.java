package indi.sly.system.services.jobs.instances.prototypes.processors.objects;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.AccessControlDefinition;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityDescriptorObjectTaskInitializer extends ATaskInitializer {
    public SecurityDescriptorObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ObjectManager.class).getFactory().rebuildSecurityDescriptor(handle);

        this.register("getSummary", this::getSummary, TransactionType.INDEPENDENCE);
        this.register("isInherit", this::isInherit, TransactionType.INDEPENDENCE);
        this.register("setInherit", this::setInherit, TransactionType.INDEPENDENCE);
        this.register("getOwners", this::getOwners, TransactionType.INDEPENDENCE);
        this.register("setOwners", this::setOwners, TransactionType.INDEPENDENCE);
        this.register("setPermissions", this::setPermissions, TransactionType.INDEPENDENCE);
        this.register("setAudits", this::setAudits, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getSummary(TaskRunConsumer run, TaskContentObject content) {
        SecurityDescriptorObject securityDescriptor = content.getCacheableObject();

        content.setResult(securityDescriptor.getSummary());
    }

    private void isInherit(TaskRunConsumer run, TaskContentObject content) {
        SecurityDescriptorObject securityDescriptor = content.getCacheableObject();

        content.setResult(securityDescriptor.isInherit());
    }

    private void setInherit(TaskRunConsumer run, TaskContentObject content) {
        SecurityDescriptorObject securityDescriptor = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        boolean inherit = ObjectUtil.transferFromString(Boolean.class, parameters.getFirst());

        securityDescriptor.setInherit(inherit);
    }

    private void getOwners(TaskRunConsumer run, TaskContentObject content) {
        SecurityDescriptorObject securityDescriptor = content.getCacheableObject();

        content.setResult(securityDescriptor.getOwners());
    }

    private void setOwners(TaskRunConsumer run, TaskContentObject content) {
        SecurityDescriptorObject securityDescriptor = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Set<UUID> owners = ObjectUtil.transferSetFromString(UUID.class, parameters.getFirst());

        securityDescriptor.setOwners(owners);
    }

    private void setPermissions(TaskRunConsumer run, TaskContentObject content) {
        SecurityDescriptorObject securityDescriptor = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Set<AccessControlDefinition> permissions = ObjectUtil.transferSetFromString(AccessControlDefinition.class, parameters.getFirst());

        securityDescriptor.setPermissions(permissions);
    }

    private void setAudits(TaskRunConsumer run, TaskContentObject content) {
        SecurityDescriptorObject securityDescriptor = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Set<AccessControlDefinition> audits = ObjectUtil.transferSetFromString(AccessControlDefinition.class, parameters.getFirst());

        securityDescriptor.setAudits(audits);
    }
}
