package indi.sly.system.kernel.objects.instances.prototypes.processors;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.instances.prototypes.NamelessFolderContentObject;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NamelessFolderTypeInitializer extends AInfoTypeInitializer {
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
        if (ValueUtil.isAnyNullOrEmpty(childInfo.getId())) {
            throw new StatusNotSupportedException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

        this.lockProcedure(info, LockType.WRITE);
        try {
            InfoWildcardRecord wildcard = new InfoWildcardRecord(childInfo.getId());
            if (infoRepository.countRelation(info, wildcard) > 0) {
                throw new StatusAlreadyExistedException();
            }

            InfoRelationEntity infoRelation = new InfoRelationEntity();
            infoRelation.setId(childInfo.getId());
            infoRelation.setParentId(info.getId());
            infoRelation.setType(childInfo.getType());
            infoRelation.setName(childInfo.getName());

            infoRepository.addRelation(infoRelation);
        } finally {
            this.unlockProcedure(info, LockType.WRITE);
        }
    }

    @Override
    public InfoSummaryDefinition getChildProcedure(InfoEntity info, IdentifierRecord identification) {
        if (identification.type() != UUID.class) {
            throw new StatusNotSupportedException();
        }

        UUID childInfoID = UUIDUtil.readFormBytes(identification.value());
        InfoSummaryDefinition infoSummary = new InfoSummaryDefinition();

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

        this.lockProcedure(info, LockType.READ);
        try {
            InfoRelationEntity infoRelation = infoRepository.getRelation(info, childInfoID);

            infoSummary.setId(infoRelation.getId());
            infoSummary.setType(infoRelation.getType());
            infoSummary.setName(infoRelation.getName());
        } finally {
            this.unlockProcedure(info, LockType.READ);
        }

        return infoSummary;
    }

    @Override
    public Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, InfoWildcardRecord wildcard) {
        if (wildcard.type() != UUID.class) {
            throw new StatusNotSupportedException();
        }

        Set<InfoSummaryDefinition> infoSummaries = new HashSet<>();

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

        this.lockProcedure(info, LockType.READ);
        try {
            List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info, wildcard);
            for (InfoRelationEntity infoRelation : infoRelations) {
                InfoSummaryDefinition infoSummary = new InfoSummaryDefinition();
                infoSummary.setId(infoRelation.getId());
                infoSummary.setType(infoRelation.getType());
                infoSummary.setName(infoRelation.getName());

                infoSummaries.add(infoSummary);
            }
        } finally {
            this.unlockProcedure(info, LockType.READ);
        }

        return CollectionUtil.unmodifiable(infoSummaries);
    }

    @Override
    public void deleteChildProcedure(InfoEntity info, IdentifierRecord identification) {
        if (identification.type() != UUID.class) {
            throw new StatusNotSupportedException();
        }

        UUID childInfoID = UUIDUtil.readFormBytes(identification.value());

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

        this.lockProcedure(info, LockType.WRITE);
        try {
            InfoRelationEntity infoRelation = infoRepository.getRelation(info, childInfoID);

            infoRepository.deleteRelation(infoRelation);
        } finally {
            this.unlockProcedure(info, LockType.WRITE);
        }
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenRecord infoOpen) {
        return NamelessFolderContentObject.class;
    }
}
