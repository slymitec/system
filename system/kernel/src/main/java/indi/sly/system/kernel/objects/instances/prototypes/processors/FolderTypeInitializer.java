package indi.sly.system.kernel.objects.instances.prototypes.processors;

import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusIsUsedException;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.instances.prototypes.FolderContentObject;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.*;
import jakarta.inject.Named;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FolderTypeInitializer extends AInfoTypeInitializer {
    @Override
    public UUID getPoolId(UUID id, UUID type) {
        return this.coreManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORY_ID;
    }

    @Override
    public void deleteProcedure(InfoEntity info) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

        if (infoRepository.countRelation(info, null) > 0) {
            throw new StatusIsUsedException();
        }
    }

    @Override
    public void createChildProcedure(InfoEntity info, InfoEntity childInfo) {
        if (StringUtil.isNameIllegal(childInfo.getName())) {
            throw new StatusNotSupportedException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

        InfoWildcardRecord wildcard = new InfoWildcardRecord(childInfo.getName());
        if (infoRepository.countRelation(info, wildcard) > 0) {
            throw new StatusAlreadyExistedException();
        }

        InfoRelationEntity infoRelation = new InfoRelationEntity();
        infoRelation.setId(childInfo.getId());
        infoRelation.setParentId(info.getId());
        infoRelation.setType(childInfo.getType());
        infoRelation.setName(childInfo.getName());

        infoRepository.addRelation(infoRelation);
    }

    @Override
    public InfoSummaryRecord getChildProcedure(InfoEntity info, IdentifierRecord identifier) {
        if (identifier.type() != String.class) {
            throw new StatusNotSupportedException();
        }

        String childInfoName = StringUtil.readFormBytes(identifier.value());
        InfoSummaryRecord infoSummary;

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

        InfoRelationEntity infoRelation = infoRepository.getRelation(info, childInfoName);

        infoSummary = new InfoSummaryRecord(infoRelation.getId(), infoRelation.getType(), infoRelation.getName());

        return infoSummary;
    }

    @Override
    public Set<InfoSummaryRecord> queryChildProcedure(InfoEntity info, InfoWildcardRecord wildcard) {
        Set<InfoSummaryRecord> infoSummaries = new HashSet<>();

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

        List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info, wildcard);
        for (InfoRelationEntity infoRelation : infoRelations) {
            InfoSummaryRecord infoSummary = new InfoSummaryRecord(infoRelation.getId(), infoRelation.getType(), infoRelation.getName());

            infoSummaries.add(infoSummary);
        }

        return CollectionUtil.unmodifiable(infoSummaries);
    }

    @Override
    public void deleteChildProcedure(InfoEntity info, IdentifierRecord identifier) {
        if (identifier.type() != String.class) {
            throw new StatusNotSupportedException();
        }

        String childInfoName = StringUtil.readFormBytes(identifier.value());

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

        InfoRelationEntity infoRelation = infoRepository.getRelation(info, childInfoName);

        infoRepository.deleteRelation(infoRelation);
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, @Nullable InfoOpenRecord infoOpen) {
        return FolderContentObject.class;
    }
}
