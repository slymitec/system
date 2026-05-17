package indi.sly.system.kernel.services.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.CommunicationRepositoryObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.*;
import indi.sly.system.kernel.services.instances.prototypes.ServiceContentObject;
import indi.sly.system.kernel.services.instances.values.ServiceDefinition;
import indi.sly.system.kernel.services.values.ServiceEntryDefinition;
import jakarta.inject.Named;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
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

        InfoObject servicesInfo = objectManager.get(new PathDefinition(List.of(new IdentifierDefinition("Services"))));

        InfoObject service = servicesInfo.createChild(kernelConfiguration.SERVICE_INSTANCE_SERVICE_ID, new IdentifierDefinition(serviceId));
        SecurityDescriptorObject securityDescriptor = service.getSecurityDescriptor();
        Set<AccessControlDefinition> permissions = new HashSet<>();
        AccessControlDefinition permission = new AccessControlDefinition();
        permission.setUserId(new UserIDDefinition(serviceId, UserType.ROLE));
        permission.setScope(AccessControlScopeType.THIS);
        permission.setValue(PermissionType.FULLCONTROL_ALLOW);
        permissions.add(permission);
        securityDescriptor.setPermissions(permissions);

        service.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);

        ServiceContentObject serviceContent = (ServiceContentObject) service.getContent();
        serviceContent.set(definition);

        service.close();
    }

    public void build(UUID serviceId, List<UUID> dependencies, String secret, PathDefinition path, UUID accountId,
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

        KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();
        RLock lock = communicationRepository.getReadWriteLock(kernelConfiguration.SERVICE_TABLE_LOCK_ID).writeLock();
        RMap<UUID, ServiceEntryDefinition> ServiceTable = communicationRepository.getMap(kernelConfiguration.SERVICE_TABLE_ID);

        try {
            lock.lock();

            if (ServiceTable.containsKey(serviceId)) {
                throw new StatusRelationshipErrorException();
            }
        } finally {
            lock.unlock();
        }

        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);

        InfoObject servicesInfo = objectManager.get(new PathDefinition(List.of(new IdentifierDefinition("Services"))));

        servicesInfo.deleteChild(new IdentifierDefinition(serviceId));
    }
}
