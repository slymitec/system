package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.CacheDurationType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.prototypes.processors.*;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessCacheEntity;
import indi.sly.system.kernel.processes.values.ProcessChildCacheEntity;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.values.ProcessInfoEntryCacheEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessFactory extends AFactory {
    public ProcessFactory() {
        this.processResolvers = new CopyOnWriteArrayList<>();
        this.processCreatorResolvers = new CopyOnWriteArrayList<>();
        this.processEndResolvers = new CopyOnWriteArrayList<>();
    }

    protected final List<AProcessResolver> processResolvers;
    protected final List<AProcessCreateResolver> processCreatorResolvers;
    protected final List<AProcessEndResolver> processEndResolvers;
    private UUID processCacheRepositoryId;
    private UUID processChildCacheRepositoryId;
    private UUID processInfoEntryCacheRepositoryId;

    @Override
    public void init() {
        this.processCreatorResolvers.add(this.coreManager.create(ProcessCreateCheckResolver.class));
        this.processCreatorResolvers.add(this.coreManager.create(ProcessCreateContextResolver.class));
        this.processCreatorResolvers.add(this.coreManager.create(ProcessCreateInfoTableResolver.class));
        this.processCreatorResolvers.add(this.coreManager.create(ProcessCreateNotifyParentResolver.class));
        this.processCreatorResolvers.add(this.coreManager.create(ProcessCreateSessionResolver.class));
        this.processCreatorResolvers.add(this.coreManager.create(ProcessCreateStatisticsResolver.class));
        this.processCreatorResolvers.add(this.coreManager.create(ProcessCreateTokenResolver.class));
        this.processCreatorResolvers.add(this.coreManager.create(ProcessCreateTokenRuleResolver.class));
        this.processEndResolvers.add(this.coreManager.create(ProcessEndCommunicationResolver.class));
        this.processEndResolvers.add(this.coreManager.create(ProcessEndSessionResolver.class));
        this.processEndResolvers.add(this.coreManager.create(ProcessEndInfoTableResolver.class));
        this.processEndResolvers.add(this.coreManager.create(ProcessEndNotifyParentResolver.class));
        this.processResolvers.add(this.coreManager.create(ProcessMemberResolver.class));
        this.processResolvers.add(this.coreManager.create(ProcessSelfResolver.class));
        this.processResolvers.add(this.coreManager.create(ProcessStatisticsResolver.class));

        Collections.sort(this.processResolvers);
        Collections.sort(this.processCreatorResolvers);
        Collections.sort(this.processEndResolvers);

        this.processCacheRepositoryId = UUID.randomUUID();
        this.processChildCacheRepositoryId = UUID.randomUUID();
        this.processInfoEntryCacheRepositoryId = UUID.randomUUID();
        this.coreManager.getObjectCollection().addById(SpaceType.KERNEL, this.processCacheRepositoryId, this.coreManager.create(ProcessCacheRepositoryObject.class));
        this.coreManager.getObjectCollection().addById(SpaceType.KERNEL, this.processChildCacheRepositoryId, this.coreManager.create(ProcessChildRepositoryObject.class));
        this.coreManager.getObjectCollection().addById(SpaceType.KERNEL, this.processInfoEntryCacheRepositoryId, this.coreManager.create(ProcessInfoEntryRepositoryObject.class));
    }

    public ProcessObject createProcess(ProcessProcessorMediator processorMediator, ProcessCacheEntity cache) {
        DateTimeObject dateTime = this.coreManager.getDateTime();

        ProcessObject process = this.coreManager.create(ProcessObject.class);

        process.setCache(cache);
        process.factory = this;
        process.processorMediator = processorMediator;

        ProcessStatisticsObject processStatistics = process.getStatistics();
        processStatistics.setDate(DateTimeType.ACCESS, dateTime.getCurrent());

        return process;
    }

    public void lockProcess(ProcessCacheEntity cache, long lock) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

        processRepository.lock(processRepository.get(cache.getProcessId()), lock);
    }

    public void unlockProcess(ProcessCacheEntity cache, long lock) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

        processRepository.unlock(processRepository.get(cache.getProcessId()), lock);
    }

    public ProcessObject buildProcess(ProcessEntity process) {
        ProcessProcessorMediator processorMediator = this.coreManager.create(ProcessProcessorMediator.class);
        for (AProcessResolver processResolver : this.processResolvers) {
            processResolver.resolve(process, processorMediator);
        }

        ProcessCacheEntity cache = new ProcessCacheEntity();
        cache.setProcessId(process.getId());
        cache.setDuration(CacheDurationType.NORMAL);
        cache.setCacheRepositoryId(this.processCacheRepositoryId);

        return this.createProcess(processorMediator, cache);
    }

    public ProcessObject rebuildProcess(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessCacheRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processCacheRepositoryId);
        ProcessCacheEntity cache = cacheRepository.get(handle);

        return this.rebuildProcess(cache);
    }

    public ProcessObject rebuildProcess(ProcessCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessCacheRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processCacheRepositoryId);
        cacheRepository.refresh(cache);

        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();
        ProcessEntity process = processRepository.get(cache.getProcessId());

        ProcessProcessorMediator processorMediator = this.coreManager.create(ProcessProcessorMediator.class);
        for (AProcessResolver processResolver : this.processResolvers) {
            processResolver.resolve(process, processorMediator);
        }

        return this.createProcess(processorMediator, cache);
    }

    public ProcessCreateBuilder createProcessCreator(ProcessObject parentProcess) {
        ProcessLifeProcessorMediator processorMediator = this.coreManager.create(ProcessLifeProcessorMediator.class);
        for (AProcessCreateResolver processCreatorResolver : this.processCreatorResolvers) {
            processCreatorResolver.resolve(processorMediator);
        }

        ProcessCreateBuilder processCreateBuilder = this.coreManager.create(ProcessCreateBuilder.class);

        processCreateBuilder.processorMediator = processorMediator;
        processCreateBuilder.factory = this;
        processCreateBuilder.parentProcess = parentProcess;

        return processCreateBuilder;
    }

    public ProcessEndBuilder createProcessEnd(ProcessObject parentProcess, ProcessObject process) {
        if (ObjectUtil.isAnyNull(process)) {
            throw new ConditionParametersException();
        }

        ProcessLifeProcessorMediator processorMediator = this.coreManager.create(ProcessLifeProcessorMediator.class);
        for (AProcessEndResolver processEndResolver : this.processEndResolvers) {
            processEndResolver.resolve(processorMediator);
        }

        ProcessEndBuilder processEndBuilder = this.coreManager.create(ProcessEndBuilder.class);

        processEndBuilder.processorMediator = processorMediator;
        processEndBuilder.factory = this;
        processEndBuilder.parentProcess = parentProcess;
        processEndBuilder.process = process;

        return processEndBuilder;
    }

    private ProcessStatusObject createProcessStatus(ProcessProcessorMediator processorMediator, ProcessObject process, ProcessChildCacheEntity cache) {
        ProcessStatusObject processStatus = this.coreManager.create(ProcessStatusObject.class);

        processStatus.setBase(process);
        processStatus.setCache(cache);
        processStatus.factory = this;
        processStatus.processorMediator = processorMediator;

        return processStatus;
    }

    public ProcessStatusObject buildProcessStatus(ProcessProcessorMediator processorMediator, ProcessObject process) {
        ProcessChildCacheEntity cache = new ProcessChildCacheEntity();

        cache.setProcess(process.getCache());
        cache.setDuration(CacheDurationType.NORMAL);
        cache.setCacheRepositoryId(this.processChildCacheRepositoryId);

        return this.createProcessStatus(processorMediator, process, cache);
    }

    public ProcessStatusObject rebuildProcessStatus(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        ProcessChildCacheEntity cache = cacheRepository.get(handle);

        return this.rebuildProcessStatus(cache);
    }

    public ProcessStatusObject rebuildProcessStatus(ProcessChildCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        cacheRepository.refresh(cache);

        ProcessObject process = this.rebuildProcess(cache.getProcess());

        return process.getStatus();
    }

    private ProcessCommunicationObject createProcessCommunication(ProcessProcessorMediator processorMediator, ProcessObject process, ProcessChildCacheEntity cache) {
        ProcessCommunicationObject processCommunication = this.coreManager.create(ProcessCommunicationObject.class);

        processCommunication.setBase(process);
        processCommunication.setCache(cache);
        processCommunication.processorMediator = processorMediator;

        return processCommunication;
    }

    public ProcessCommunicationObject buildProcessCommunication(ProcessProcessorMediator processorMediator, ProcessObject process) {
        ProcessChildCacheEntity cache = new ProcessChildCacheEntity();

        cache.setProcess(process.getCache());
        cache.setDuration(CacheDurationType.NORMAL);
        cache.setCacheRepositoryId(this.processChildCacheRepositoryId);

        return this.createProcessCommunication(processorMediator, process, cache);
    }

    public ProcessCommunicationObject rebuildProcessCommunication(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        ProcessChildCacheEntity cache = cacheRepository.get(handle);

        return this.rebuildProcessCommunication(cache);
    }

    public ProcessCommunicationObject rebuildProcessCommunication(ProcessChildCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        cacheRepository.refresh(cache);

        ProcessObject process = this.rebuildProcess(cache.getProcess());

        return process.getCommunication();
    }

    private ProcessInfoTableObject createProcessInfoTable(ProcessProcessorMediator processorMediator, ProcessObject process, ProcessChildCacheEntity cache) {
        ProcessInfoTableObject processInfoTable = this.coreManager.create(ProcessInfoTableObject.class);

        processInfoTable.setBase(process);
        processInfoTable.setCache(cache);
        processInfoTable.processorMediator = processorMediator;

        return processInfoTable;
    }

    public ProcessInfoTableObject buildProcessInfoTable(ProcessProcessorMediator processorMediator, ProcessObject process) {
        ProcessChildCacheEntity cache = new ProcessChildCacheEntity();

        cache.setProcess(process.getCache());
        cache.setDuration(CacheDurationType.NORMAL);
        cache.setCacheRepositoryId(this.processChildCacheRepositoryId);

        return this.createProcessInfoTable(processorMediator, process, cache);
    }

    public ProcessInfoTableObject rebuildProcessInfoTable(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        ProcessChildCacheEntity cache = cacheRepository.get(handle);

        return this.rebuildProcessInfoTable(cache);
    }

    public ProcessInfoTableObject rebuildProcessInfoTable(ProcessChildCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        cacheRepository.refresh(cache);

        ProcessObject process = this.rebuildProcess(cache.getProcess());

        return process.getInfoTable();
    }

    private ProcessInfoEntryObject createProcessInfoEntry(ProcessProcessorMediator processorMediator, ProcessInfoTableObject processInfoTable, ProcessInfoEntryCacheEntity cache) {
        ProcessInfoEntryObject processInfoEntry = this.coreManager.create(ProcessInfoEntryObject.class);

        processInfoEntry.setBase(processInfoTable);
        processInfoEntry.setCache(cache);
        processInfoEntry.processorMediator = processorMediator;

        return processInfoEntry;
    }

    public ProcessInfoEntryObject buildProcessInfoEntry(ProcessProcessorMediator processorMediator, ProcessInfoTableObject processInfoTable, UUID index) {
        ProcessInfoEntryCacheEntity cache = new ProcessInfoEntryCacheEntity();

        cache.setProcessInfoTable(processInfoTable.getCache());
        cache.setIndex(index);
        cache.setDuration(CacheDurationType.NORMAL);
        cache.setCacheRepositoryId(this.processChildCacheRepositoryId);

        return this.createProcessInfoEntry(processorMediator, processInfoTable, cache);
    }

    public ProcessInfoEntryObject rebuildProcessInfoEntry(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessInfoEntryRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processInfoEntryCacheRepositoryId);
        ProcessInfoEntryCacheEntity cache = cacheRepository.get(handle);

        return this.rebuildProcessInfoEntry(cache);
    }

    public ProcessInfoEntryObject rebuildProcessInfoEntry(ProcessInfoEntryCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessInfoEntryRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processInfoEntryCacheRepositoryId);
        cacheRepository.refresh(cache);

        ProcessInfoTableObject processInfoTable = this.rebuildProcessInfoTable(cache.getProcessInfoTable());

        return processInfoTable.getByIndex(cache.getIndex());
    }

    public void updateProcessInfoEntry(ProcessInfoEntryCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessInfoEntryRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processInfoEntryCacheRepositoryId);
        cacheRepository.update(cache);
    }

    private ProcessContextObject createProcessContext(ProcessProcessorMediator processorMediator, ProcessObject process, ProcessChildCacheEntity cache) {
        ProcessContextObject processContext = this.coreManager.create(ProcessContextObject.class);

        processContext.setBase(process);
        processContext.setCache(cache);
        processContext.processorMediator = processorMediator;

        return processContext;
    }

    public ProcessContextObject buildProcessContext(ProcessProcessorMediator processorMediator, ProcessObject process) {
        ProcessChildCacheEntity cache = new ProcessChildCacheEntity();

        cache.setProcess(process.getCache());
        cache.setDuration(CacheDurationType.NORMAL);
        cache.setCacheRepositoryId(this.processChildCacheRepositoryId);

        return this.createProcessContext(processorMediator, process, cache);
    }

    public ProcessContextObject rebuildProcessContext(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        ProcessChildCacheEntity cache = cacheRepository.get(handle);

        return this.rebuildProcessContext(cache);
    }

    public ProcessContextObject rebuildProcessContext(ProcessChildCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        cacheRepository.refresh(cache);

        ProcessObject process = this.rebuildProcess(cache.getProcess());

        return process.getContext();
    }

    private ProcessSessionObject createProcessSession(ProcessProcessorMediator processorMediator, ProcessObject process, ProcessChildCacheEntity cache) {
        ProcessSessionObject processSession = this.coreManager.create(ProcessSessionObject.class);

        processSession.setBase(process);
        processSession.setCache(cache);
        processSession.factory = this;
        processSession.processorMediator = processorMediator;

        return processSession;
    }

    public ProcessSessionObject buildProcessSession(ProcessProcessorMediator processorMediator, ProcessObject process) {
        ProcessChildCacheEntity cache = new ProcessChildCacheEntity();

        cache.setProcess(process.getCache());
        cache.setDuration(CacheDurationType.NORMAL);
        cache.setCacheRepositoryId(this.processChildCacheRepositoryId);

        return this.createProcessSession(processorMediator, process, cache);
    }

    public ProcessSessionObject rebuildProcessSession(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        ProcessChildCacheEntity cache = cacheRepository.get(handle);

        return this.rebuildProcessSession(cache);
    }

    public ProcessSessionObject rebuildProcessSession(ProcessChildCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        cacheRepository.refresh(cache);

        ProcessObject process = this.rebuildProcess(cache.getProcess());

        return process.getSession();
    }

    private ProcessStatisticsObject createProcessStatistics(ProcessProcessorMediator processorMediator, ProcessObject process, ProcessChildCacheEntity cache) {
        ProcessStatisticsObject processStatistics = this.coreManager.create(ProcessStatisticsObject.class);

        processStatistics.setBase(process);
        processStatistics.setCache(cache);
        processStatistics.factory = this;
        processStatistics.processorMediator = processorMediator;

        return processStatistics;
    }

    public ProcessStatisticsObject buildProcessStatistics(ProcessProcessorMediator processorMediator, ProcessObject process) {
        ProcessChildCacheEntity cache = new ProcessChildCacheEntity();

        cache.setProcess(process.getCache());
        cache.setDuration(CacheDurationType.NORMAL);
        cache.setCacheRepositoryId(this.processChildCacheRepositoryId);

        return this.createProcessStatistics(processorMediator, process, cache);
    }

    public ProcessStatisticsObject rebuildProcessStatistics(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        ProcessChildCacheEntity cache = cacheRepository.get(handle);

        return this.rebuildProcessStatistics(cache);
    }

    public ProcessStatisticsObject rebuildProcessStatistics(ProcessChildCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        cacheRepository.refresh(cache);

        ProcessObject process = this.rebuildProcess(cache.getProcess());

        return process.getStatistics();
    }

    private ProcessTokenObject createProcessToken(ProcessProcessorMediator processorMediator, ProcessObject process, ProcessChildCacheEntity cache) {
        ProcessTokenObject processToken = this.coreManager.create(ProcessTokenObject.class);

        processToken.setBase(process);
        processToken.setCache(cache);
        processToken.factory = this;
        processToken.processorMediator = processorMediator;

        return processToken;
    }

    public ProcessTokenObject buildProcessToken(ProcessProcessorMediator processorMediator, ProcessObject process) {
        ProcessChildCacheEntity cache = new ProcessChildCacheEntity();

        cache.setProcess(process.getCache());
        cache.setDuration(CacheDurationType.NORMAL);
        cache.setCacheRepositoryId(this.processChildCacheRepositoryId);

        return this.createProcessToken(processorMediator, process, cache);
    }

    public ProcessTokenObject rebuildProcessToken(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        ProcessChildCacheEntity cache = cacheRepository.get(handle);

        return this.rebuildProcessToken(cache);
    }

    public ProcessTokenObject rebuildProcessToken(ProcessChildCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        ProcessChildRepositoryObject cacheRepository = memoryManager.getCacheRepository(this.processChildCacheRepositoryId);
        cacheRepository.refresh(cache);

        ProcessObject process = this.rebuildProcess(cache.getProcess());

        return process.getToken();
    }
}
