package indi.sly.system.kernel.services;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.CommunicationRepositoryObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessContextObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.services.instances.prototypes.ServiceContentObject;
import indi.sly.system.kernel.services.instances.values.ServiceStartType;
import indi.sly.system.kernel.services.prototypes.ServiceFactory;
import indi.sly.system.kernel.services.values.ServiceEntryDefinition;
import jakarta.inject.Named;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
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

    private void start(UUID serviceId, Set<UUID> serviceIdJobs) {
        if (ValueUtil.isAnyNullOrEmpty(serviceId) || ObjectUtil.isAnyNull(serviceIdJobs)) {
            throw new ConditionParametersException();
        }
        if (!serviceIdJobs.add(serviceId)) {
            throw new StatusRelationshipErrorException();
        }

        KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        UserManager userManager = this.coreManager.getManager(UserManager.class);

        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();
        RLock lock = communicationRepository.getReadWriteLock(kernelConfiguration.SERVICE_TABLE_LOCK_ID).writeLock();
        RMap<UUID, ServiceEntryDefinition> serviceTable = communicationRepository.getMap(kernelConfiguration.SERVICE_TABLE_ID);

        PathDefinition path = new PathDefinition(List.of(new IdentifierDefinition("Services"), new IdentifierDefinition(serviceId)));

        InfoObject serviceInfo = objectManager.get(path);

        UUID serviceInfoIndex = serviceInfo.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);

        ServiceContentObject serviceContent = (ServiceContentObject) serviceInfo.getContent();

        if (LogicalUtil.isAnyEqual(serviceContent.getStart(), ServiceStartType.DISABLED)) {
            serviceInfo.close();

            throw new StatusRelationshipErrorException();
        }

        List<UUID> startedServiceDependencies = new ArrayList<>();
        List<UUID> occupiedServiceDependencies = new ArrayList<>();
        InfoObject executeInfo = null;
        UUID executeInfoIndex = null;
        lock.lock();
        try {
            for (UUID dependencyServerId : serviceContent.getDependencies()) {
                ServiceEntryDefinition dependencyServiceEntry = serviceTable.getOrDefault(dependencyServerId, null);

                if (ObjectUtil.isAnyNull(dependencyServiceEntry)) {
                    this.start(dependencyServerId, serviceIdJobs);
                    startedServiceDependencies.add(dependencyServerId);
                } else {
                    dependencyServiceEntry.setOccupy(dependencyServiceEntry.getOccupy() + 1);
                    occupiedServiceDependencies.add(dependencyServerId);
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

            ProcessObject process = processManager.create(authorize, executeInfoIndex, serviceContent.getParameters(), workFolderPath);

            ProcessContextObject processContext = process.getContext();
            processContext.setEnvironmentVariables(serviceContent.getEnvironmentVariables());
            ProcessInfoTableObject processInfoTable = process.getInfoTable();
            processInfoTable.inherit(serviceInfoIndex);
            ProcessInfoEntryObject processInfoTableEntry = processInfoTable.getByIndex(serviceInfoIndex);
            processInfoTableEntry.setUnsupportedDelete(true);

            ServiceEntryDefinition serviceTableEntry = new ServiceEntryDefinition();
            serviceTableEntry.getDependencies().addAll(serviceContent.getDependencies());
            serviceTableEntry.setOccupy(1L);
            serviceTableEntry.setProcessId(process.getId());
            serviceTable.put(serviceId, serviceTableEntry);
        } catch (Exception e) {
            serviceInfo.close();

            for (UUID dependencyServerId : startedServiceDependencies) {
                this.stop(dependencyServerId);
            }
            for (UUID dependencyServerId : occupiedServiceDependencies) {
                ServiceEntryDefinition serviceDependencyTableEntry = serviceTable.getOrDefault(dependencyServerId, null);

                if (ObjectUtil.allNotNull(serviceDependencyTableEntry)) {
                    serviceDependencyTableEntry.setOccupy(serviceDependencyTableEntry.getOccupy() - 1);
                }
            }

            if (ObjectUtil.allNotNull(executeInfo) && !ValueUtil.isAnyNullOrEmpty(executeInfoIndex)) {
                executeInfo.close();
            }

            throw e;
        } finally {
            lock.unlock();
        }
    }

    public void start(UUID serviceId) {
        this.start(serviceId, new HashSet<>());
    }

    public void stop(UUID serviceId) {
        if (ValueUtil.isAnyNullOrEmpty(serviceId)) {
            throw new ConditionParametersException();
        }

        KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        CommunicationRepositoryObject communicationRepository = memoryManager.getCommunicationRepository();
        RLock lock = communicationRepository.getReadWriteLock(kernelConfiguration.SERVICE_TABLE_LOCK_ID).writeLock();
        RMap<UUID, ServiceEntryDefinition> serviceTable = communicationRepository.getMap(kernelConfiguration.SERVICE_TABLE_ID);

        lock.lock();
        try {
            ServiceEntryDefinition serviceTableEntry = serviceTable.getOrDefault(serviceId, null);
            if (ObjectUtil.isAnyNull(serviceTableEntry)) {
                throw new StatusNotExistedException();
            }

            if (serviceTableEntry.getOccupy() > 1) {
                throw new StatusRelationshipErrorException();
            }

            UUID processId = serviceTableEntry.getProcessId();
            processManager.end(processId);

            serviceTableEntry.setOccupy(0L);

            List<UUID> serverDependencies = serviceTableEntry.getDependencies();
            for (UUID dependencyServerId : serverDependencies) {
                ServiceEntryDefinition serviceDependencyTableEntry = serviceTable.getOrDefault(dependencyServerId, null);

                if (ObjectUtil.allNotNull(serviceDependencyTableEntry)) {

                    if (serviceDependencyTableEntry.getOccupy() == 1) {
                        try {
                            this.stop(dependencyServerId);
                        } catch (RuntimeException _) {
                        }
                    } else if (serviceDependencyTableEntry.getOccupy() != 0) {
                        serviceDependencyTableEntry.setOccupy(serviceDependencyTableEntry.getOccupy() - 1);
                    }
                }
            }

            serviceTable.remove(serviceId);
        } finally {
            lock.unlock();
        }
    }
}