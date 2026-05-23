package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.enviroment.values.CacheDurationType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.core.prototypes.ObjectCollectionObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.memory.repositories.prototypes.CacheRepositoryObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.prototypes.processors.*;
import indi.sly.system.kernel.objects.prototypes.mediators.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.DumpCacheEntity;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;
import indi.sly.system.kernel.objects.values.InfoContentCacheEntity;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.SecurityDescriptorCacheEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoFactory extends AFactory {
    public InfoFactory() {
        this.infoResolvers = new CopyOnWriteArrayList<>();
    }

    private final List<IInfoResolver> infoResolvers;

    @Override
    public void init() {
        this.infoResolvers.add(this.coreManager.create(InfoCheckConditionResolver.class));
        this.infoResolvers.add(this.coreManager.create(InfoCloseThenDeleteIfTemporaryResolver.class));
        this.infoResolvers.add(this.coreManager.create(InfoDateResolver.class));
        this.infoResolvers.add(this.coreManager.create(InfoDumpResolver.class));
        this.infoResolvers.add(this.coreManager.create(InfoOpenOrCloseResolver.class));
        this.infoResolvers.add(this.coreManager.create(InfoParentResolver.class));
        this.infoResolvers.add(this.coreManager.create(InfoProcessAndThreadStatisticsResolver.class));
        this.infoResolvers.add(this.coreManager.create(InfoProcessInfoTableCloseResolver.class));
        this.infoResolvers.add(this.coreManager.create(InfoProcessInfoTableResolver.class));
        this.infoResolvers.add(this.coreManager.create(InfoSecurityDescriptorCreateResolver.class));
        this.infoResolvers.add(this.coreManager.create(InfoSecurityDescriptorResolver.class));
        this.infoResolvers.add(this.coreManager.create(InfoSelfResolver.class));
        this.infoResolvers.add(this.coreManager.create(InfoTypeInitializerResolver.class));
        Collections.sort(this.infoResolvers);
    }

    public InfoObject getRootInfo() {
        KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        return this.coreManager.getObjectCollection().getById(SpaceType.KERNEL, kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID);
    }

    public void buildRootInfo() {
        KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository =
                memoryManager.getInfoRepository(kernelConfiguration.MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORY_ID);
        InfoEntity info =
                infoRepository.get(kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID);

        ObjectCollectionObject coreObjectRepository = this.coreManager.getObjectCollection();

        coreObjectRepository.addById(SpaceType.KERNEL, kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID, this.buildInfo(info, null));
    }

    private InfoObject createInfo(InfoProcessorMediator processorMediator, InfoCacheEntity cache) {
        InfoObject info = this.coreManager.create(InfoObject.class);

        info.setCache(cache);
        info.factory = this;
        info.processorMediator = processorMediator;

        return info;
    }

    public InfoObject buildInfo(InfoEntity info, InfoCacheEntity parentInfoCache) {
        if (ObjectUtil.isAnyNull(info)) {
            throw new ConditionParametersException();
        }

        InfoProcessorMediator processorMediator = this.coreManager.create(InfoProcessorMediator.class);
        for (IInfoResolver infoResolver : this.infoResolvers) {
            infoResolver.resolve(info, processorMediator);
        }

        TypeManager typeManager = this.coreManager.create(TypeManager.class);
        TypeObject typeObject = typeManager.get(info.getType());
        UUID poolId = typeObject.getInitializer().getPoolId(info.getId(), info.getType());

        InfoCacheEntity cache = new InfoCacheEntity();

        cache.setInfoId(info.getId());
        cache.setPoolId(poolId);
        cache.setDuration(CacheDurationType.NORMAL);

        if (ObjectUtil.allNotNull(parentInfoCache)) {
            IdentifierDefinition identifier;
            if (StringUtil.isNameIllegal(info.getName())) {
                identifier = new IdentifierDefinition(info.getId());
            } else {
                identifier = new IdentifierDefinition(info.getName());
            }
            cache.setPath(new PathDefinition(parentInfoCache.getPath(), identifier));
        } else {
            cache.setPath(new PathDefinition(List.of()));
        }

        return this.createInfo(processorMediator, cache);
    }

    public InfoObject rebuildInfo(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        InfoCacheEntity cache = cacheRepository.get(InfoCacheEntity.class, handle);

        return this.rebuildInfo(cache);
    }

    public InfoObject rebuildInfo(InfoCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(InfoCacheEntity.class, cache);

        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(cache.getPoolId());
        InfoEntity info = infoRepository.get(cache.getInfoId());

        InfoProcessorMediator processorMediator = this.coreManager.create(InfoProcessorMediator.class);
        for (IInfoResolver infoResolver : this.infoResolvers) {
            infoResolver.resolve(info, processorMediator);
        }

        return this.createInfo(processorMediator, cache);
    }

    private SecurityDescriptorObject createSecurityDescriptor(InfoProcessorMediator processorMediator, InfoObject info, SecurityDescriptorCacheEntity cache) {
        SecurityDescriptorObject securityDescriptor = this.coreManager.create(SecurityDescriptorObject.class);

        securityDescriptor.setBase(info);
        securityDescriptor.setCache(cache);
        securityDescriptor.setProcessorMediator(processorMediator);

        return securityDescriptor;
    }

    public SecurityDescriptorObject buildSecurityDescriptor(InfoProcessorMediator processorMediator, InfoObject info, SecurityDescriptorCacheEntity cache) {
        cache.setInfo(info.getCache());
        cache.setDuration(CacheDurationType.NORMAL);

        return this.createSecurityDescriptor(processorMediator, info, cache);
    }

    public SecurityDescriptorObject rebuildSecurityDescriptor(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        SecurityDescriptorCacheEntity cache = cacheRepository.get(SecurityDescriptorCacheEntity.class, handle);

        return this.rebuildSecurityDescriptor(cache);
    }

    public SecurityDescriptorObject rebuildSecurityDescriptor(SecurityDescriptorCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(SecurityDescriptorCacheEntity.class, cache);

        InfoObject info = this.rebuildInfo(cache.getInfo());

        return info.getSecurityDescriptor();
    }

    private AInfoContentObject createInfoContent(InfoProcessorMediator processorMediator, InfoObject info, InfoContentCacheEntity cache, Class<? extends AInfoContentObject> infoContentType) {
        AInfoContentObject infoContent = this.coreManager.create(infoContentType);

        infoContent.setCache(cache);
        infoContent.setBase(info);
        info.processorMediator = processorMediator;

        return infoContent;
    }

    public AInfoContentObject buildInfoContent(InfoProcessorMediator processorMediator, InfoObject info, Class<? extends AInfoContentObject> infoContentType) {
        InfoContentCacheEntity cache = new InfoContentCacheEntity();

        cache.setInfo(info.getCache());
        cache.setDuration(CacheDurationType.NORMAL);

        return this.createInfoContent(processorMediator, info, cache, infoContentType);
    }

    public AInfoContentObject rebuildInfoContent(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        InfoContentCacheEntity cache = cacheRepository.get(InfoContentCacheEntity.class, handle);

        return this.rebuildInfoContent(cache);
    }

    public AInfoContentObject rebuildInfoContent(InfoContentCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(InfoContentCacheEntity.class, cache);

        InfoObject info = this.rebuildInfo(cache.getInfo());

        return info.getContent();
    }

    private DumpObject createDump(DumpCacheEntity cache) {
        DumpObject dump = this.coreManager.create(DumpObject.class);

        dump.setCache(cache);

        return dump;
    }

    public DumpObject buildDump(DumpCacheEntity cache) {
        if (ObjectUtil.isAnyNull(cache)) {
            throw new ConditionParametersException();
        }

        cache.setDuration(CacheDurationType.NORMAL);

        return this.createDump(cache);
    }

    public DumpObject rebuildDump(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        DumpCacheEntity cache = cacheRepository.get(DumpCacheEntity.class, handle);

        return this.rebuildDump(cache);
    }

    public DumpObject rebuildDump(DumpCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(DumpCacheEntity.class, cache);

        return this.createDump(cache);
    }
}
