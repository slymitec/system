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
import indi.sly.system.kernel.objects.lang.InfoQueryChildPredicate;
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
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info);

        if (!infoRelations.isEmpty()) {
            throw new StatusIsUsedException();
        }
    }

    @Override
    public void createChildProcedure(InfoEntity info, InfoEntity childInfo) {
        if (ValueUtil.isAnyNullOrEmpty(childInfo.getID())) {
            throw new StatusNotSupportedException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        try {
            this.lockProcedure(info, LockType.WRITE);

            List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info);
            for (InfoRelationEntity infoRelation : infoRelations) {
                if (infoRelation.getID().equals(childInfo.getID())) {
                    throw new StatusAlreadyExistedException();
                }
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

        UUID childInfoID = UUIDUtil.readFormBytes(identification.getID());
        InfoSummaryDefinition infoSummary = new InfoSummaryDefinition();

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject entityRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        try {
            this.lockProcedure(info, LockType.READ);

            List<InfoRelationEntity> infoRelations = entityRepository.listRelation(info);

            boolean isFinished = false;
            for (InfoRelationEntity infoRelation : infoRelations) {
                if (infoRelation.getID().equals(childInfoID)) {
                    infoSummary.setID(infoRelation.getID());
                    infoSummary.setType(infoRelation.getType());
                    infoSummary.setName(infoRelation.getName());

                    isFinished = true;
                    break;
                }
            }

            if (!isFinished) {
                throw new StatusNotExistedException();
            }
        } finally {
            this.lockProcedure(info, LockType.NONE);
        }

        return infoSummary;
    }

    @Override
    public Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, InfoQueryChildPredicate wildcard) {
        Set<InfoSummaryDefinition> infoSummaries = new HashSet<>();

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        try {
            this.lockProcedure(info, LockType.READ);

            List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info);
            for (InfoRelationEntity infoRelation : infoRelations) {
                InfoSummaryDefinition infoSummary = new InfoSummaryDefinition();
                infoSummary.setID(infoRelation.getID());
                infoSummary.setType(infoRelation.getType());
                infoSummary.setName(infoRelation.getName());

                if (wildcard.test(infoSummary)) {
                    infoSummaries.add(infoSummary);
                }
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

        UUID childInfoID = UUIDUtil.readFormBytes(identification.getID());

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        try {
            this.lockProcedure(info, LockType.WRITE);

            List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info);

            boolean isFinished = false;
            for (InfoRelationEntity infoRelation : infoRelations) {
                if (infoRelation.getID().equals(childInfoID)) {
                    infoRepository.deleteRelation(infoRelation);

                    isFinished = true;
                    break;
                }
            }

            if (!isFinished) {
                throw new StatusNotExistedException();
            }
        } finally {
            this.lockProcedure(info, LockType.NONE);
        }
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        return NamelessFolderContentObject.class;
    }
}
