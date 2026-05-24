package indi.sly.system.kernel.services;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ServiceRepositoryObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessContextObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.values.ProcessAdditionalCreatorDefinition;
import indi.sly.system.kernel.processes.values.ProcessContextType;
import indi.sly.system.kernel.processes.values.ProcessCreatorDefinition;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.services.instances.prototypes.ServiceContentObject;
import indi.sly.system.kernel.services.instances.prototypes.processors.ServiceTypeInitializer;
import indi.sly.system.kernel.services.instances.values.ServiceStartType;
import indi.sly.system.kernel.services.prototypes.ServiceFactory;
import indi.sly.system.kernel.services.values.ServiceStatusEntity;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceManager extends AManager {
    private ServiceFactory factory;

    public ServiceFactory getFactory() {
        return this.factory;
    }

    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
            this.factory = this.coreManager.create(ServiceFactory.class);
            this.factory.init();
        } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
            TypeManager typeManager = this.coreManager.getManager(TypeManager.class);

            KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

            long attribute = LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SHARED_READ,
                    TypeInitializerAttributeType.CAN_BE_INHERITED, TypeInitializerAttributeType.CAN_NOT_CHANGE_OWNER,
                    TypeInitializerAttributeType.HAS_AUDIT, TypeInitializerAttributeType.HAS_CONTENT,
                    TypeInitializerAttributeType.HAS_PERMISSION, TypeInitializerAttributeType.HAS_PROPERTIES);
            Set<UUID> childTypes = Set.of();
            AInfoTypeInitializer typeInitializer = this.coreManager.create(ServiceTypeInitializer.class);

            typeManager.create(kernelConfiguration.SERVICE_TYPES_INSTANCE_SERVICE_ID,
                    kernelConfiguration.SERVICE_TYPES_INSTANCE_SERVICE_NAME, attribute, childTypes, typeInitializer);
        }
    }

    @Override
    public void shutdown() {
    }

    public void createService(UUID serviceId, List<UUID> dependencies, String secret, PathDefinition path, UUID accountId,
                              long mode, long start, Map<String, String> environmentVariables, String parameters) {
        if (ValueUtil.isAnyNullOrEmpty(serviceId, accountId, parameters, secret) || ObjectUtil.isAnyNull(dependencies, path, environmentVariables)) {
            throw new ConditionParametersException();
        }

        this.factory.build(serviceId, dependencies, secret, path, accountId, mode, start, environmentVariables, parameters);
    }

    public void deleteService(UUID serviceId) {
        this.factory.deleteService(serviceId);
    }

    private void start(UUID serviceId, boolean independence, Set<UUID> serviceIdJobs) {
        if (ValueUtil.isAnyNullOrEmpty(serviceId) || ObjectUtil.isAnyNull(serviceIdJobs)) {
            throw new ConditionParametersException();
        }
        if (!serviceIdJobs.add(serviceId)) {
            throw new StatusRelationshipErrorException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        UserManager userManager = this.coreManager.getManager(UserManager.class);

        ServiceRepositoryObject serviceRepository = memoryManager.getServiceRepository();

        PathDefinition path = new PathDefinition(List.of(new IdentifierDefinition("Services"), new IdentifierDefinition(serviceId)));

        InfoObject serviceInfo = objectManager.get(path);

        UUID serviceInfoIndex = serviceInfo.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);

        ServiceContentObject serviceContent = (ServiceContentObject) serviceInfo.getContent();

        if (LogicalUtil.isAnyEqual(serviceContent.getStart(), ServiceStartType.DISABLED)) {
            serviceInfo.close();

            throw new StatusRelationshipErrorException();
        }

        ServiceStatusEntity serviceStatus = new ServiceStatusEntity();
        serviceStatus.setId(UUIDUtil.createRandom());
        serviceStatus.setIndependence(independence);

        InfoObject executeInfo = null;
        UUID executeInfoIndex = null;
        try {
            for (UUID dependencyServerId : serviceContent.getDependencies()) {
                if (!serviceRepository.contain(dependencyServerId)) {
                    this.start(dependencyServerId, false, serviceIdJobs);
                }

                ServiceStatusEntity dependencyService = serviceRepository.get(dependencyServerId);

                serviceRepository.lock(dependencyService, LockType.WRITE);

                try {
                    serviceStatus.addDependency(dependencyService);
                } finally {
                    serviceRepository.unlock(dependencyService, LockType.WRITE);
                }
            }

            AccountAuthorizationObject authorize = userManager.authorize(serviceContent.getAccountId());

            PathDefinition executePath = serviceContent.getPath();

            ArrayList<IdentifierDefinition> workFolderPathList = new ArrayList<>(executePath.get());
            if (!workFolderPathList.isEmpty()) {
                workFolderPathList.removeLast();
            }
            PathDefinition workFolderPath = new PathDefinition(workFolderPathList);

            executeInfo = objectManager.get(executePath);
            executeInfoIndex = executeInfo.open(InfoOpenAttributeType.OPEN_ONLY_READ);

            ProcessAdditionalCreatorDefinition processAdditionalCreator = new ProcessAdditionalCreatorDefinition();
            processAdditionalCreator.setContextType(ProcessContextType.EXECUTABLE_SERVICE);

            ProcessObject process = processManager.create(authorize, executeInfoIndex, serviceContent.getParameters(), workFolderPath, processAdditionalCreator);

            ProcessContextObject processContext = process.getContext();
            processContext.setEnvironmentVariables(serviceContent.getEnvironmentVariables());
            ProcessInfoTableObject processInfoTable = process.getInfoTable();
            processInfoTable.inherit(serviceInfoIndex);
            ProcessInfoEntryObject processInfoTableEntry = processInfoTable.getByIndex(serviceInfoIndex);
            processInfoTableEntry.setUnsupportedDelete(true);

            serviceStatus.setProcessId(process.getId());

            serviceRepository.add(serviceStatus);
        } catch (Exception e) {
            if (ObjectUtil.allNotNull(executeInfo) && !ValueUtil.isAnyNullOrEmpty(executeInfoIndex)) {
                executeInfo.close();
            }

            serviceInfo.close();

            throw e;
        }
    }

    public void start(UUID serviceId) {
        this.start(serviceId, true, new HashSet<>());
    }

    public void stop(UUID serviceId) {
        if (ValueUtil.isAnyNullOrEmpty(serviceId)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        ServiceRepositoryObject serviceRepository = memoryManager.getServiceRepository();

        ServiceStatusEntity serviceStatus = serviceRepository.get(serviceId);

        serviceRepository.lock(serviceStatus, LockType.WRITE);
        try {

            if (!serviceStatus.getDependents().isEmpty()) {
                throw new StatusRelationshipErrorException();
            }

            UUID processId = serviceStatus.getProcessId();
            processManager.end(processId);

            Set<ServiceStatusEntity> serverDependencies = serviceStatus.getDependencies();
            for (ServiceStatusEntity dependencyService : serverDependencies) {
                serviceRepository.lock(dependencyService, LockType.WRITE);
                try {
                    serviceStatus.removeDependency(dependencyService);
                } finally {
                    serviceRepository.unlock(dependencyService, LockType.WRITE);
                }

                if (dependencyService.getDependents().isEmpty() && !dependencyService.isIndependence()) {
                    this.stop(dependencyService.getId());
                }
            }
        } finally {
            serviceRepository.unlock(serviceStatus, LockType.WRITE);
        }

        serviceRepository.delete(serviceStatus);
    }
}