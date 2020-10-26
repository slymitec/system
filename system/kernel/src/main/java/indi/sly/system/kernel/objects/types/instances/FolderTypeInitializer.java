package indi.sly.system.kernel.objects.types.instances;

import indi.sly.system.common.exceptions.StatusAlreadyExistedException;
import indi.sly.system.common.exceptions.StatusNotExistedException;
import indi.sly.system.common.exceptions.StatusNotReadyException;
import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.AEntityRepositoryObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoRelationEntity;
import indi.sly.system.kernel.objects.entities.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.prototypes.StatusOpenDefinition;
import indi.sly.system.kernel.objects.types.ATypeInitializer;
import indi.sly.system.kernel.processes.dumps.DumpDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;
import java.util.function.Predicate;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FolderTypeInitializer extends ATypeInitializer {
    @Override
    public void install() {
    }

    @Override
    public void uninstall() {
    }

    @Override
    public UUID getPoolID(UUID id, UUID type) {
        return this.factoryManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID;
    }

    @Override
    public void createProcedure(InfoEntity info) {
    }

    @Override
    public void deleteProcedure(InfoEntity info) {
        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AEntityRepositoryObject entityRepository = memoryManager.getEntityRepository(this.getPoolID(info.getID(), info.getType()));

        List<InfoRelationEntity> infoRelations = entityRepository.listRelation(info);

        if (infoRelations.size() > 0) {
            throw new StatusNotReadyException();
        }
    }

    @Override
    public void getProcedure(InfoEntity info) {
    }

    @Override
    public void dumpProcedure(InfoEntity info, DumpDefinition dump) {
    }

    @Override
    public void openProcedure(InfoEntity info, StatusOpenDefinition statusOpen, long openAttribute, Object... arguments) {
    }

    @Override
    public void closeProcedure(InfoEntity info, StatusOpenDefinition statusOpen) {
    }

    @Override
    public void createChildProcedure(InfoEntity info, InfoEntity childInfo) {
        if (StringUtils.isNameIllegal(childInfo.getName())) {
            throw new StatusNotSupportedException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AEntityRepositoryObject entityRepository = memoryManager.getEntityRepository(this.getPoolID(info.getID(), info.getType()));

        this.lockProcedure(info, LockTypes.WRITE);

        List<InfoRelationEntity> infoRelations = entityRepository.listRelation(info);
        for (InfoRelationEntity infoRelation : infoRelations) {
            if (infoRelation.getName().equals(childInfo.getName())) {
                this.lockProcedure(info, LockTypes.NONE);
                throw new StatusAlreadyExistedException();
            }
        }

        InfoRelationEntity infoRelation = new InfoRelationEntity();
        infoRelation.setID(childInfo.getID());
        infoRelation.setParentID(info.getID());
        infoRelation.setType(childInfo.getType());
        infoRelation.setName(childInfo.getName());

        entityRepository.addRelation(infoRelation);

        this.lockProcedure(info, LockTypes.NONE);
    }

    @Override
    public InfoSummaryDefinition getChildProcedure(InfoEntity info, Identification identification) {
        if (identification.getType() != String.class) {
            throw new StatusNotSupportedException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AEntityRepositoryObject entityRepository = memoryManager.getEntityRepository(this.getPoolID(info.getID(), info.getType()));

        String childName = StringUtils.readFormBytes(identification.getID());

        List<InfoRelationEntity> infoRelations = entityRepository.listRelation(info);
        for (InfoRelationEntity infoRelation : infoRelations) {
            if (infoRelation.getName().equals(childName)) {
                InfoSummaryDefinition infoSummary = new InfoSummaryDefinition();
                infoSummary.setID(infoRelation.getID());
                infoSummary.setType(infoRelation.getType());
                infoSummary.setName(infoRelation.getName());

                return infoSummary;
            }
        }

        throw new StatusNotExistedException();
    }

    @Override
    public Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, Predicate<InfoSummaryDefinition> wildcard) {
        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AEntityRepositoryObject entityRepository = memoryManager.getEntityRepository(this.getPoolID(info.getID(), info.getType()));

        this.lockProcedure(info, LockTypes.WRITE);

        List<InfoRelationEntity> infoRelations = entityRepository.listRelation(info);
        Set<InfoSummaryDefinition> infoSummaries = new HashSet<>();
        for (InfoRelationEntity infoRelation : infoRelations) {
            InfoSummaryDefinition infoSummary = new InfoSummaryDefinition();
            infoSummary.setID(infoRelation.getID());
            infoSummary.setType(infoRelation.getType());
            infoSummary.setName(infoRelation.getName());

            if (wildcard.test(infoSummary)) {
                infoSummaries.add(infoSummary);
            }
        }

        return Collections.unmodifiableSet(infoSummaries);
    }

    @Override
    public void renameChildProcedure(InfoEntity info, Identification oldIdentification, Identification newIdentification) {
        if (oldIdentification.getType() != String.class || newIdentification.getType() != String.class) {
            throw new StatusNotSupportedException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AEntityRepositoryObject entityRepository = memoryManager.getEntityRepository(this.getPoolID(info.getID(), info.getType()));

        this.lockProcedure(info, LockTypes.WRITE);

        String oldChildName = StringUtils.readFormBytes(oldIdentification.getID());
        String newChildName = StringUtils.readFormBytes(oldIdentification.getID());

        List<InfoRelationEntity> infoRelations = entityRepository.listRelation(info);
        for (InfoRelationEntity infoRelation : infoRelations) {
            if (infoRelation.getName().equals(oldChildName)) {
                infoRelation.setName(newChildName);

                this.lockProcedure(info, LockTypes.NONE);
                return;
            }
        }

        this.lockProcedure(info, LockTypes.NONE);
        throw new StatusNotExistedException();
    }

    @Override
    public void deleteChildProcedure(InfoEntity info, Identification identification) {
        if (identification.getType() != String.class) {
            throw new StatusNotSupportedException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AEntityRepositoryObject entityRepository = memoryManager.getEntityRepository(this.getPoolID(info.getID(), info.getType()));

        this.lockProcedure(info, LockTypes.WRITE);

        String childName = StringUtils.readFormBytes(identification.getID());

        List<InfoRelationEntity> infoRelations = entityRepository.listRelation(info);
        for (InfoRelationEntity infoRelation : infoRelations) {
            if (infoRelation.getName().equals(childName)) {
                infoRelations.remove(infoRelation);

                this.lockProcedure(info, LockTypes.NONE);
                return;
            }
        }

        this.lockProcedure(info, LockTypes.NONE);
        throw new StatusNotExistedException();
    }

    @Override
    public Class<? extends AInfoContentObject> getContentProcedureType(InfoEntity info, StatusOpenDefinition statusOpen) {
        return FolderContentObject.class;
    }

    @Override
    public void refreshPropertiesProcedure(InfoEntity info, StatusOpenDefinition statusOpen) {
    }
}
