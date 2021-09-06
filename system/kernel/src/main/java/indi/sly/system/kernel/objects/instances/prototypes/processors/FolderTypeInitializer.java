package indi.sly.system.kernel.objects.instances.prototypes.processors;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.instances.prototypes.FolderContentObject;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FolderTypeInitializer extends AInfoTypeInitializer {
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
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info);

        if (infoRelations.size() > 0) {
            throw new StatusIsUsedException();
        }
    }

    @Override
    public void getProcedure(InfoEntity info, IdentificationDefinition identification) {
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
        if (StringUtil.isNameIllegal(childInfo.getName())) {
            throw new StatusNotSupportedException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        try {
            this.lockProcedure(info, LockType.WRITE);

            List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info);
            for (InfoRelationEntity infoRelation : infoRelations) {
                if (infoRelation.getName().equals(childInfo.getName())) {
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
        if (identification.getType() != String.class) {
            throw new StatusNotSupportedException();
        }

        String childInfoName = StringUtil.readFormBytes(identification.getID());
        InfoSummaryDefinition infoSummary = new InfoSummaryDefinition();

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        try {
            this.lockProcedure(info, LockType.READ);

            List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info);

            boolean isFinished = false;
            for (InfoRelationEntity infoRelation : infoRelations) {
                if (infoRelation.getName().equals(childInfoName)) {
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
    public Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, Predicate1<InfoSummaryDefinition> wildcard) {
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
    public void renameChildProcedure(InfoEntity info, IdentificationDefinition oldIdentification,
                                     IdentificationDefinition newIdentification) {
        throw new StatusNotSupportedException();
    }

    @Override
    public void deleteChildProcedure(InfoEntity info, IdentificationDefinition identification) {
        if (identification.getType() != String.class) {
            throw new StatusNotSupportedException();
        }

        String childInfoName = StringUtil.readFormBytes(identification.getID());

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                info.getType()));

        try {
            this.lockProcedure(info, LockType.WRITE);

            List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info);

            boolean isFinished = false;
            for (InfoRelationEntity infoRelation : infoRelations) {
                if (infoRelation.getName().equals(childInfoName)) {
                    infoRepository.deleteRelation(infoRelation);

                    isFinished = true;
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
        return FolderContentObject.class;
    }

    @Override
    public void refreshPropertiesProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
    }
}
