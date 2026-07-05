package indi.sly.system.kernel.services.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ServiceRepositoryObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.*;
import indi.sly.system.kernel.services.instances.prototypes.ServiceContentObject;
import indi.sly.system.kernel.services.instances.values.ServiceDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceFactory extends AFactory {
    @Override
    public void init() {
    }

    private void createService(UUID serviceId, ServiceDefinition definition) {
        if (ValueUtil.isAnyNullOrEmpty(serviceId) || ObjectUtil.isAnyNull(definition)) {
            throw new ConditionParametersException();
        }

        KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);

        InfoObject servicesInfo = objectManager.get(new PathRecord(List.of(new IdentifierRecord("Services"))));

        InfoObject service = servicesInfo.createChild(kernelConfiguration.SERVICE_TYPES_INSTANCE_SERVICE_ID, new IdentifierRecord(serviceId));
        SecurityDescriptorObject securityDescriptor = service.getSecurityDescriptor();
        Set<AccessControlDefinition> permissions = new HashSet<>();
        AccessControlDefinition permission = new AccessControlDefinition();
        permission.setUserId(new UserIdDefinition(serviceId, UserType.ROLE));
        permission.setScope(AccessControlScopeType.THIS);
        permission.setValue(PermissionType.FULLCONTROL_ALLOW);
        permissions.add(permission);
        securityDescriptor.setPermissions(permissions);

        service.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);

        ServiceContentObject serviceContent = (ServiceContentObject) service.getContent();
        serviceContent.set(definition);

        service.close();
    }

    public void build(UUID serviceId, List<UUID> dependencies, String secret, PathRecord path, UUID accountId,
                      long mode, long start, Map<String, String> environmentVariables, String parameters) {
        if (ValueUtil.isAnyNullOrEmpty(serviceId, accountId, parameters, secret) || ObjectUtil.isAnyNull(dependencies, path, environmentVariables)) {
            throw new ConditionParametersException();
        }

        ServiceDefinition service = new ServiceDefinition();
        service.getDependencies().addAll(dependencies);
        service.setSecret(secret);
        service.setPath(path);
        service.setAccountId(accountId);
        service.setMode(mode);
        service.setStart(start);
        service.getEnvironmentVariables().putAll(environmentVariables);
        service.setParameters(parameters);

        this.createService(serviceId, service);
    }

    public void deleteService(UUID serviceId) {
        if (ValueUtil.isAnyNullOrEmpty(serviceId)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ServiceRepositoryObject serviceRepository = memoryManager.getServiceRepository();

        if(serviceRepository.contain(serviceId)){
            throw new StatusRelationshipErrorException();
        }

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);

        InfoObject servicesInfo = objectManager.get(new PathRecord(List.of(new IdentifierRecord("Services"))));

        servicesInfo.deleteChild(new IdentifierRecord(serviceId));
    }
}
