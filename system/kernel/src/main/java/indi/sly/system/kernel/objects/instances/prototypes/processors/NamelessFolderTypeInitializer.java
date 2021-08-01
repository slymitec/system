package indi.sly.system.kernel.objects.instances.prototypes.processors;

import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.instances.prototypes.NamelessFolderContentObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoRelationEntity;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.ATypeInitializer;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;
import java.util.function.Predicate;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NamelessFolderTypeInitializer extends ATypeInitializer {
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
        AInfoRepositoryObject entityRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

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
    public void openProcedure(InfoEntity info, InfoOpenDefinition infoOpen, long openAttribute,
                              Object... arguments) {
    }

    @Override
    public void closeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
    }

    @Override
    public void createChildProcedure(InfoEntity info, InfoEntity childInfo) {
        if (ValueUtil.isAnyNullOrEmpty(childInfo.getID())) {
            throw new StatusNotSupportedException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject entityRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        this.lockProcedure(info, LockType.WRITE);

        List<InfoRelationEntity> infoRelations = entityRepository.listRelation(info);
        for (InfoRelationEntity infoRelation : infoRelations) {
            if (infoRelation.getID().equals(childInfo.getID())) {
                this.lockProcedure(info, LockType.NONE);
                throw new StatusAlreadyExistedException();
            }
        }

        InfoRelationEntity infoRelation = new InfoRelationEntity();
        infoRelation.setID(childInfo.getID());
        infoRelation.setParentID(info.getID());
        infoRelation.setType(childInfo.getType());
        infoRelation.setName(childInfo.getName());

        entityRepository.addRelation(infoRelation);

        this.lockProcedure(info, LockType.NONE);
    }

    @Override
    public InfoSummaryDefinition getChildProcedure(InfoEntity info, IdentificationDefinition identification) {
        if (identification.getType() != String.class) {
            throw new StatusNotSupportedException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject entityRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        UUID childID = UUIDUtil.readFormBytes(identification.getID());

        List<InfoRelationEntity> infoRelations = entityRepository.listRelation(info);
        for (InfoRelationEntity infoRelation : infoRelations) {
            if (infoRelation.getID().equals(childID)) {
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
        AInfoRepositoryObject entityRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        this.lockProcedure(info, LockType.WRITE);

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
    public void renameChildProcedure(InfoEntity info, IdentificationDefinition oldIdentification,
                                     IdentificationDefinition newIdentification) {
        throw new StatusNotSupportedException();
    }

    @Override
    public void deleteChildProcedure(InfoEntity info, IdentificationDefinition identification) {
        if (identification.getType() != String.class) {
            throw new StatusNotSupportedException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject entityRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        this.lockProcedure(info, LockType.WRITE);

        UUID childID = UUIDUtil.readFormBytes(identification.getID());

        List<InfoRelationEntity> infoRelations = entityRepository.listRelation(info);
        for (InfoRelationEntity infoRelation : infoRelations) {
            if (infoRelation.getID().equals(childID)) {
                entityRepository.deleteRelation(infoRelation);

                this.lockProcedure(info, LockType.NONE);
                return;
            }
        }

        this.lockProcedure(info, LockType.NONE);
        throw new StatusNotExistedException();
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        return NamelessFolderContentObject.class;
    }

    @Override
    public void refreshPropertiesProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
    }
}
