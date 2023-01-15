package indi.sly.system.kernel.objects.instances.prototypes.processors;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.instances.prototypes.NamelessFolderContentObject;
import indi.sly.system.kernel.objects.values.InfoWildcardDefinition;
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
    public UUID getPoolID(UUID id, UUID type) {
        return this.factoryManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID;
    }

    @Override
    public void deleteProcedure(InfoEntity info) {
        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(), info.getType()));

        if (infoRepository.countRelation(info, null) > 0) {
            throw new StatusIsUsedException();
        }
    }

    @Override
    public void createChildProcedure(InfoEntity info, InfoEntity childInfo) {
        if (ValueUtil.isAnyNullOrEmpty(childInfo.getID())) {
            throw new StatusNotSupportedException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(), info.getType()));

        try {
            this.lockProcedure(info, LockType.WRITE);

            InfoWildcardDefinition wildcard = new InfoWildcardDefinition(childInfo.getID());
            if (infoRepository.countRelation(info, wildcard) > 0) {
                throw new StatusAlreadyExistedException();
            }

            InfoRelationEntity infoRelation = new InfoRelationEntity();
            infoRelation.setID(childInfo.getID());
            infoRelation.setParentID(info.getID());
            infoRelation.setType(childInfo.getType());
            infoRelation.setName(childInfo.getName());

            infoRepository.addRelation(infoRelation);
        } finally {
            this.lockProcedure(info, LockType.NONE);
        }
    }

    @Override
    public InfoSummaryDefinition getChildProcedure(InfoEntity info, IdentificationDefinition identification) {
        if (identification.getType() != UUID.class) {
            throw new StatusNotSupportedException();
        }

        UUID childInfoID = UUIDUtil.readFormBytes(identification.getValue());
        InfoSummaryDefinition infoSummary = new InfoSummaryDefinition();

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(), info.getType()));

        try {
            this.lockProcedure(info, LockType.READ);

            InfoRelationEntity infoRelation = infoRepository.getRelation(info, childInfoID);

            infoSummary.setID(infoRelation.getID());
            infoSummary.setType(infoRelation.getType());
            infoSummary.setName(infoRelation.getName());
        } finally {
            this.lockProcedure(info, LockType.NONE);
        }

        return infoSummary;
    }

    @Override
    public Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, InfoWildcardDefinition wildcard) {
        if (wildcard.getType() != UUID.class) {
            throw new StatusNotSupportedException();
        }

        Set<InfoSummaryDefinition> infoSummaries = new HashSet<>();

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(), info.getType()));

        try {
            this.lockProcedure(info, LockType.READ);

            List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info, wildcard);
            for (InfoRelationEntity infoRelation : infoRelations) {
                InfoSummaryDefinition infoSummary = new InfoSummaryDefinition();
                infoSummary.setID(infoRelation.getID());
                infoSummary.setType(infoRelation.getType());
                infoSummary.setName(infoRelation.getName());

                infoSummaries.add(infoSummary);
            }
        } finally {
            this.lockProcedure(info, LockType.NONE);
        }

        return CollectionUtil.unmodifiable(infoSummaries);
    }

    @Override
    public void deleteChildProcedure(InfoEntity info, IdentificationDefinition identification) {
        if (identification.getType() != UUID.class) {
            throw new StatusNotSupportedException();
        }

        UUID childInfoID = UUIDUtil.readFormBytes(identification.getValue());

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(), info.getType()));

        try {
            this.lockProcedure(info, LockType.WRITE);

            InfoRelationEntity infoRelation = infoRepository.getRelation(info, childInfoID);

            infoRepository.deleteRelation(infoRelation);
        } finally {
            this.lockProcedure(info, LockType.NONE);
        }
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        return NamelessFolderContentObject.class;
    }
}
