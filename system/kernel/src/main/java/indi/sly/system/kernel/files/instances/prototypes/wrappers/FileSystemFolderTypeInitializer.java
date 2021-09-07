package indi.sly.system.kernel.files.instances.prototypes.wrappers;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.files.instances.prototypes.FileSystemFolderContentObject;
import indi.sly.system.kernel.files.instances.values.FileSystemEntryDefinition;
import indi.sly.system.kernel.files.instances.values.FileSystemLocationType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.lang.InfoQueryChildPredicate;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileSystemFolderTypeInitializer extends AInfoTypeInitializer {
    @Override
    public UUID getPoolID(UUID id, UUID type) {
        return this.factoryManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID;
    }

    @Override
    public void createProcedure(InfoEntity info) {
        FileSystemEntryDefinition entry = new FileSystemEntryDefinition();

        entry.setType(FileSystemLocationType.REPOSITORY);

        info.setContent(ObjectUtil.transferToByteArray(entry));
    }

    @Override
    public void deleteProcedure(InfoEntity info) {
        FileSystemEntryDefinition entry = ObjectUtil.transferFromByteArray(info.getContent());
        assert entry != null;

        if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                    info.getType()));

            List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info);

            if (infoRelations.size() > 0) {
                throw new StatusIsUsedException();
            }
        } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
            File infoFolder = new File(StringUtil.readFormBytes(entry.getValue()));

            if (!infoFolder.exists() || !infoFolder.isDirectory()) {
                throw new StatusNotExistedException();
            }

            String[] childInfoNames = infoFolder.list();
            assert childInfoNames != null;

            if (childInfoNames.length > 0) {
                throw new StatusIsUsedException();
            }
        }
    }

    @Override
    public void getProcedure(InfoEntity info, IdentificationDefinition identification) {
        if (identification.getType() == String.class) {
            String childInfoName = StringUtil.readFormBytes(identification.getID());
            if (!childInfoName.equals(info.getName())) {
                info.setName(childInfoName);
            }
        }
    }

    @Override
    public void createChildProcedure(InfoEntity info, InfoEntity childInfo) {
        if (StringUtil.isNameIllegal(childInfo.getName())) {
            throw new StatusNotSupportedException();
        }

        FileSystemEntryDefinition entry = ObjectUtil.transferFromByteArray(info.getContent());
        assert entry != null;

        if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
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

                FileSystemEntryDefinition childFileSystemEntry = ObjectUtil.transferFromByteArray(childInfo.getContent());
                assert childFileSystemEntry != null;
                childFileSystemEntry.setType(entry.getType());
                childInfo.setContent(ObjectUtil.transferToByteArray(childFileSystemEntry));

                InfoRelationEntity infoRelation = new InfoRelationEntity();
                infoRelation.setID(childInfo.getID());
                infoRelation.setParentID(info.getID());
                infoRelation.setType(childInfo.getType());
                infoRelation.setName(childInfo.getName());

                infoRepository.addRelation(infoRelation);
            } finally {
                this.lockProcedure(info, LockType.NONE);
            }
        } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
            File infoFolder = new File(StringUtil.readFormBytes(entry.getValue()));
            File infoRelationFolder = new File(infoFolder.getAbsolutePath() + "_Relation");

            if (!infoFolder.exists() || !infoFolder.isDirectory() || !infoRelationFolder.exists() || !infoRelationFolder.isDirectory()) {
                throw new StatusNotExistedException();
            }

            String[] childInfoNames = infoFolder.list((dir, name) -> childInfo.getName().equals(name));
            assert childInfoNames != null;

            if (childInfoNames.length > 0) {
                throw new StatusAlreadyExistedException();
            }

            File childInfoFile = new File(infoFolder.getAbsolutePath() + "/" + childInfo.getName());
            File childInfoRelationFile = new File(infoRelationFolder.getAbsolutePath() + "/" + childInfo.getName());

            FileSystemEntryDefinition childEntry = ObjectUtil.transferFromByteArray(childInfo.getContent());
            assert childEntry != null;
            childEntry.setType(entry.getType());
            childEntry.setValue(StringUtil.writeToBytes(childInfoFile.getAbsolutePath().replace("\\", "/")));
            childInfo.setContent(ObjectUtil.transferToByteArray(childEntry));

            try {
                if (!childInfoFile.createNewFile()) {
                    throw new StatusUnexpectedException();
                }
            } catch (IOException e) {
                throw new StatusUnexpectedException();
            }
            byte[] childInfoRelationID = UUIDUtil.writeToBytes(childInfo.getID());
            byte[] childInfoRelationType = UUIDUtil.writeToBytes(childInfo.getType());
            try (FileOutputStream fileOutputStream = new FileOutputStream(childInfoRelationFile)) {
                fileOutputStream.write(childInfoRelationID);
                fileOutputStream.write(childInfoRelationType);
            } catch (IOException e) {
                throw new StatusUnexpectedException();
            }
        }
    }

    @Override
    public InfoSummaryDefinition getChildProcedure(InfoEntity info, IdentificationDefinition identification) {
        if (identification.getType() != String.class) {
            throw new StatusNotSupportedException();
        }

        FileSystemEntryDefinition entry = ObjectUtil.transferFromByteArray(info.getContent());
        assert entry != null;

        String childInfoName = StringUtil.readFormBytes(identification.getID());
        InfoSummaryDefinition infoSummary = new InfoSummaryDefinition();

        if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
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
        } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
            File infoFolder = new File(StringUtil.readFormBytes(entry.getValue()));
            File infoRelationFolder = new File(infoFolder.getAbsolutePath() + "_Relation");

            if (!infoFolder.exists() || !infoFolder.isDirectory() || !infoRelationFolder.exists() || !infoRelationFolder.isDirectory()) {
                throw new StatusNotExistedException();
            }

            String[] childInfoNames = infoFolder.list((dir, name) -> childInfoName.equals(name));
            assert childInfoNames != null;

            if (childInfoNames.length == 0) {
                throw new StatusNotExistedException();
            }

            File childInfoRelationFile = new File(infoRelationFolder.getAbsolutePath() + "/" + childInfoName);
            byte[] childInfoRelationID = new byte[16];
            byte[] childInfoRelationType = new byte[16];
            try (FileInputStream fileInputStream = new FileInputStream(childInfoRelationFile)) {
                fileInputStream.read(childInfoRelationID);
                fileInputStream.read(childInfoRelationType);
            } catch (IOException e) {
                throw new StatusUnexpectedException();
            }

            infoSummary.setID(UUIDUtil.readFormBytes(childInfoRelationID));
            infoSummary.setType(UUIDUtil.readFormBytes(childInfoRelationType));
            infoSummary.setName(childInfoName);
        }

        return infoSummary;
    }

    @Override
    public Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, InfoQueryChildPredicate wildcard) {
        FileSystemEntryDefinition entry = ObjectUtil.transferFromByteArray(info.getContent());
        assert entry != null;

        Set<InfoSummaryDefinition> infoSummaries = new HashSet<>();

        if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
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
        } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
            File infoFolder = new File(StringUtil.readFormBytes(entry.getValue()));
            File infoRelationFolder = new File(infoFolder.getAbsolutePath() + "_Relation");

            if (!infoFolder.exists() || !infoFolder.isDirectory() || !infoRelationFolder.exists() || !infoRelationFolder.isDirectory()) {
                throw new StatusNotExistedException();
            }

            String[] childInfoNames = infoFolder.list();
            assert childInfoNames != null;

            for (String childInfoName : childInfoNames) {
                File childInfoRelationFile = new File(infoRelationFolder.getAbsolutePath() + "/" + childInfoName);
                byte[] childInfoRelationID = new byte[16];
                byte[] childInfoRelationType = new byte[16];
                try (FileInputStream fileInputStream = new FileInputStream(childInfoRelationFile)) {
                    fileInputStream.read(childInfoRelationID);
                    fileInputStream.read(childInfoRelationType);
                } catch (IOException e) {
                    throw new StatusUnexpectedException();
                }

                InfoSummaryDefinition infoSummary = new InfoSummaryDefinition();
                infoSummary.setID(UUIDUtil.readFormBytes(childInfoRelationID));
                infoSummary.setType(UUIDUtil.readFormBytes(childInfoRelationType));
                infoSummary.setName(childInfoName);

                if (wildcard.test(infoSummary)) {
                    infoSummaries.add(infoSummary);
                }
            }
        }

        return CollectionUtil.unmodifiable(infoSummaries);
    }

    @Override
    public void renameChildProcedure(InfoEntity info, IdentificationDefinition oldIdentification,
                                     IdentificationDefinition newIdentification) {
        if (oldIdentification.getType() != String.class || newIdentification.getType() != String.class) {
            throw new StatusNotSupportedException();
        }

        String oldChildInfoName = StringUtil.readFormBytes(oldIdentification.getID());
        String newChildInfoName = StringUtil.readFormBytes(oldIdentification.getID());

        FileSystemEntryDefinition entry = ObjectUtil.transferFromByteArray(info.getContent());
        assert entry != null;

        if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(),
                    info.getType()));

            try {
                this.lockProcedure(info, LockType.WRITE);

                List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info);
                for (InfoRelationEntity infoRelation : infoRelations) {
                    if (infoRelation.getName().equals(oldChildInfoName)) {
                        infoRelation.setName(newChildInfoName);

                        this.lockProcedure(info, LockType.NONE);
                        return;
                    }
                }
            } finally {
                this.lockProcedure(info, LockType.NONE);
            }
        } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
            File infoFolder = new File(StringUtil.readFormBytes(entry.getValue()));
            File infoRelationFolder = new File(infoFolder.getAbsolutePath() + "_Relation");

            if (!infoFolder.exists() || !infoFolder.isDirectory() || !infoRelationFolder.exists() || !infoRelationFolder.isDirectory()) {
                throw new StatusNotExistedException();
            }

            String[] childInfoNames = infoFolder.list((dir, name) -> oldChildInfoName.equals(name));
            assert childInfoNames != null;

            if (childInfoNames.length > 0) {
                File oldChildInfoFile = new File(infoFolder.getAbsolutePath() + "/" + oldChildInfoName);
                File oldChildInfoRelationFile = new File(infoRelationFolder.getAbsolutePath() + "/" + oldChildInfoName);
                File newChildInfoFile = new File(infoFolder.getAbsolutePath() + "/" + newChildInfoName);
                File newChildInfoRelationFile = new File(infoRelationFolder.getAbsolutePath() + "/" + newChildInfoName);

                oldChildInfoFile.renameTo(newChildInfoFile);
                oldChildInfoRelationFile.renameTo(newChildInfoRelationFile);

                return;
            }
        }

        throw new StatusNotExistedException();
    }

    @Override
    public void deleteChildProcedure(InfoEntity info, IdentificationDefinition identification) {
        if (identification.getType() != String.class) {
            throw new StatusNotSupportedException();
        }

        FileSystemEntryDefinition entry = ObjectUtil.transferFromByteArray(info.getContent());
        assert entry != null;

        String childInfoName = StringUtil.readFormBytes(identification.getID());

        if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
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
        } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
            File infoFolder = new File(StringUtil.readFormBytes(entry.getValue()));
            File infoRelationFolder = new File(infoFolder.getAbsolutePath() + "_Relation");

            if (!infoFolder.exists() || !infoFolder.isDirectory() || !infoRelationFolder.exists() || !infoRelationFolder.isDirectory()) {
                throw new StatusNotExistedException();
            }

            String[] childInfoNames = infoFolder.list((dir, name) -> childInfoName.equals(name));
            assert childInfoNames != null;

            if (childInfoNames.length == 0) {
                throw new StatusNotExistedException();
            }

            File childInfoFile = new File(infoFolder.getAbsolutePath() + "/" + childInfoName);
            File childInfoRelationFile = new File(infoRelationFolder.getAbsolutePath() + "/" + childInfoName);

            childInfoFile.delete();
            childInfoRelationFile.delete();
        }
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        return FileSystemFolderContentObject.class;
    }
}
