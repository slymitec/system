package indi.sly.system.kernel.files.instances.prototypes.processors;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.environment.containers.KernelConfiguration;
import indi.sly.system.kernel.files.instances.prototypes.FileSystemFolderContentObject;
import indi.sly.system.kernel.files.instances.values.FileSystemEntryDefinition;
import indi.sly.system.kernel.files.instances.values.FileSystemLocationType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.values.InfoWildcardRecord;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoOpenRecord;
import indi.sly.system.kernel.objects.values.InfoRelationEntity;
import indi.sly.system.kernel.objects.values.InfoSummaryRecord;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

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
    public UUID getPoolId(UUID id, UUID type) {
        return this.coreManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORY_ID;
    }

    @Override
    public void createProcedure(InfoEntity info) {
        FileSystemEntryDefinition entry = new FileSystemEntryDefinition();

        entry.setType(FileSystemLocationType.REPOSITORY);
        entry.setValue(ArrayUtil.EMPTY_BYTES);

        info.setContent(ObjectUtil.transferToByteArray(entry));
    }

    @Override
    public void deleteProcedure(InfoEntity info) {
        FileSystemEntryDefinition entry = ObjectUtil.transferFromByteArray(info.getContent());
        assert entry != null;

        if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

            if (infoRepository.countRelation(info, null) > 0) {
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
    public void getProcedure(InfoEntity info, IdentifierRecord identification) {
        if (identification.type() == String.class) {
            String childInfoName = StringUtil.readFormBytes(identification.value());
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
            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));
            try {
                this.lockProcedure(info, LockType.WRITE);

                InfoWildcardRecord wildcard = new InfoWildcardRecord(childInfo.getName());
                if (infoRepository.countRelation(info, wildcard) > 0) {
                    throw new StatusAlreadyExistedException();
                }

                FileSystemEntryDefinition childEntry = ObjectUtil.transferFromByteArray(childInfo.getContent());
                assert childEntry != null;
                childEntry.setType(entry.getType());
                childEntry.setValue(ArrayUtil.EMPTY_BYTES);
                childInfo.setContent(ObjectUtil.transferToByteArray(childEntry));

                InfoRelationEntity infoRelation = new InfoRelationEntity();
                infoRelation.setId(childInfo.getId());
                infoRelation.setParentId(info.getId());
                infoRelation.setType(childInfo.getType());
                infoRelation.setName(childInfo.getName());

                infoRepository.addRelation(infoRelation);
            } finally {
                this.unlockProcedure(info, LockType.WRITE);
            }
        } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
            KernelConfiguration kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

            File infoFolder = new File(StringUtil.readFormBytes(entry.getValue()));
            File infoRelationFolder = new File(infoFolder.getAbsolutePath() + "$Relations");

            if (!infoFolder.exists() || !infoFolder.isDirectory() || !infoRelationFolder.exists() || !infoRelationFolder.isDirectory()) {
                throw new StatusNotExistedException();
            }

            String[] childInfoNames = infoFolder.list((dir, name) -> childInfo.getName().equals(name));
            assert childInfoNames != null;

            if (childInfoNames.length > 0) {
                throw new StatusAlreadyExistedException();
            }

            File childInfoFileFolder = new File(infoFolder.getAbsolutePath() + "/" + childInfo.getName());
            File childInfoRelationFile = new File(infoRelationFolder.getAbsolutePath() + "/" + childInfo.getName());

            FileSystemEntryDefinition childEntry = ObjectUtil.transferFromByteArray(childInfo.getContent());
            assert childEntry != null;
            childEntry.setType(entry.getType());
            childEntry.setValue(StringUtil.writeToBytes(childInfoFileFolder.getAbsolutePath().replace("\\", "/")));
            childInfo.setContent(ObjectUtil.transferToByteArray(childEntry));

            UUID childInfoType = childInfo.getType();

            try {
                if (childInfoType.equals(kernelConfiguration.FILES_TYPES_INSTANCE_FILE_ID)) {
                    if (!childInfoFileFolder.createNewFile()) {
                        throw new StatusUnexpectedException();
                    }
                } else if (childInfoType.equals(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID)) {
                    if (!childInfoFileFolder.mkdir()) {
                        throw new StatusUnexpectedException();
                    }
                    File childInfoRelationFolder = new File(infoFolder.getAbsolutePath() + "/" + childInfo.getName() + "$Relations");
                    if (!childInfoRelationFolder.mkdir()) {
                        throw new StatusUnexpectedException();
                    }
                }
            } catch (IOException e) {
                throw new StatusUnexpectedException();
            }
            byte[] childInfoRelationID = UUIDUtil.writeToBytes(childInfo.getId());
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
    public InfoSummaryRecord getChildProcedure(InfoEntity info, IdentifierRecord identification) {
        if (identification.type() != String.class) {
            throw new StatusNotSupportedException();
        }

        FileSystemEntryDefinition entry = ObjectUtil.transferFromByteArray(info.getContent());
        assert entry != null;

        String childInfoName = StringUtil.readFormBytes(identification.value());
        InfoSummaryRecord infoSummary = null;

        if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

            try {
                this.lockProcedure(info, LockType.READ);

                InfoRelationEntity infoRelation = infoRepository.getRelation(info, childInfoName);

                infoSummary = new InfoSummaryRecord(infoRelation.getId(), infoRelation.getType(), infoRelation.getName());
            } finally {
                this.unlockProcedure(info, LockType.READ);
            }
        } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
            File infoFolder = new File(StringUtil.readFormBytes(entry.getValue()));
            File infoRelationFolder = new File(infoFolder.getAbsolutePath() + "$Relations");

            if (!infoFolder.exists() || !infoFolder.isDirectory() || !infoRelationFolder.exists() || !infoRelationFolder.isDirectory()) {
                throw new StatusNotExistedException();
            }

            File childInfoRelationFile = new File(infoRelationFolder.getAbsolutePath() + "/" + childInfoName);

            if (!childInfoRelationFile.exists()) {
                throw new StatusNotExistedException();
            }

            byte[] childInfoRelationID = new byte[16];
            byte[] childInfoRelationType = new byte[16];
            try (FileInputStream fileInputStream = new FileInputStream(childInfoRelationFile)) {
                fileInputStream.read(childInfoRelationID);
                fileInputStream.read(childInfoRelationType);
            } catch (IOException e) {
                throw new StatusUnexpectedException();
            }

            infoSummary = new InfoSummaryRecord(UUIDUtil.readFormBytes(childInfoRelationID), UUIDUtil.readFormBytes(childInfoRelationType), childInfoName);
        }

        return infoSummary;
    }

    @Override
    public Set<InfoSummaryRecord> queryChildProcedure(InfoEntity info, InfoWildcardRecord wildcard) {
        if (wildcard.type() != String.class) {
            throw new StatusNotSupportedException();
        }

        FileSystemEntryDefinition entry = ObjectUtil.transferFromByteArray(info.getContent());
        assert entry != null;

        Set<InfoSummaryRecord> infoSummaries = new HashSet<>();

        if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

            try {
                this.lockProcedure(info, LockType.READ);

                List<InfoRelationEntity> infoRelations = infoRepository.listRelation(info, wildcard);
                for (InfoRelationEntity infoRelation : infoRelations) {
                    InfoSummaryRecord infoSummary = new InfoSummaryRecord(infoRelation.getId(), infoRelation.getType(), infoRelation.getName());

                    infoSummaries.add(infoSummary);
                }
            } finally {
                this.unlockProcedure(info, LockType.READ);
            }
        } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
            if (wildcard.type() == UUID.class) {
                throw new StatusNotSupportedException();
            }

            File infoFolder = new File(StringUtil.readFormBytes(entry.getValue()));
            File infoRelationFolder = new File(infoFolder.getAbsolutePath() + "$Relations");

            if (!infoFolder.exists() || !infoFolder.isDirectory() || !infoRelationFolder.exists() || !infoRelationFolder.isDirectory()) {
                throw new StatusNotExistedException();
            }

            String[] childInfoNames = infoFolder.list();
            assert childInfoNames != null;

            for (String childInfoName : childInfoNames) {
                File childInfoRelationFile = new File(infoRelationFolder.getAbsolutePath() + "/" + childInfoName);
                byte[] childInfoRelationId = new byte[16];
                byte[] childInfoRelationType = new byte[16];
                try (FileInputStream fileInputStream = new FileInputStream(childInfoRelationFile)) {
                    fileInputStream.read(childInfoRelationId);
                    fileInputStream.read(childInfoRelationType);
                } catch (IOException e) {
                    throw new StatusUnexpectedException();
                }

                InfoSummaryRecord infoSummary = new InfoSummaryRecord(UUIDUtil.readFormBytes(childInfoRelationId), UUIDUtil.readFormBytes(childInfoRelationType), childInfoName);

                String wildcardValue = StringUtil.readFormBytes(wildcard.value());
                if (wildcard.fuzzy()) {
                    infoSummaries.add(infoSummary);
                } else {
                    if (wildcardValue.equals(infoSummary.name())) {
                        infoSummaries.add(infoSummary);
                        break;
                    }
                }
            }
        }

        return CollectionUtil.unmodifiable(infoSummaries);
    }

    @Override
    public void renameChildProcedure(InfoEntity info, IdentifierRecord oldIdentification, IdentifierRecord newIdentification) {
        if (oldIdentification.type() != String.class || newIdentification.type() != String.class) {
            throw new StatusNotSupportedException();
        }

        String oldChildInfoName = StringUtil.readFormBytes(oldIdentification.value());
        String newChildInfoName = StringUtil.readFormBytes(oldIdentification.value());

        FileSystemEntryDefinition entry = ObjectUtil.transferFromByteArray(info.getContent());
        assert entry != null;

        if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

            try {
                this.lockProcedure(info, LockType.WRITE);

                InfoRelationEntity infoRelation = infoRepository.getRelation(info, oldChildInfoName);

                infoRelation.setName(newChildInfoName);
            } finally {
                this.unlockProcedure(info, LockType.WRITE);
            }

            return;
        } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
            File infoFolder = new File(StringUtil.readFormBytes(entry.getValue()));
            File infoRelationFolder = new File(infoFolder.getAbsolutePath() + "$Relations");

            if (!infoFolder.exists() || !infoFolder.isDirectory() || !infoRelationFolder.exists() || !infoRelationFolder.isDirectory()) {
                throw new StatusNotExistedException();
            }

            String[] childInfoNames = infoFolder.list((dir, name) -> oldChildInfoName.equals(name));
            assert childInfoNames != null;

            if (childInfoNames.length > 0) {
                File oldChildInfoFileFolder = new File(infoFolder.getAbsolutePath() + "/" + oldChildInfoName);
                File oldChildInfoRelationFile = new File(infoRelationFolder.getAbsolutePath() + "/" + oldChildInfoName);
                File newChildInfoFileFolder = new File(infoFolder.getAbsolutePath() + "/" + newChildInfoName);
                File newChildInfoRelationFile = new File(infoRelationFolder.getAbsolutePath() + "/" + newChildInfoName);

                oldChildInfoFileFolder.renameTo(newChildInfoFileFolder);
                oldChildInfoRelationFile.renameTo(newChildInfoRelationFile);

                if (oldChildInfoFileFolder.isDirectory()) {
                    File oldChildInfoRelationFolder = new File(infoFolder.getAbsolutePath() + "/" + oldChildInfoName + "$Relations");
                    File newChildInfoRelationFolder = new File(infoFolder.getAbsolutePath() + "/" + newChildInfoName + "$Relations");

                    oldChildInfoRelationFolder.renameTo(newChildInfoRelationFolder);
                }

                return;
            }
        }

        throw new StatusNotExistedException();
    }

    @Override
    public void deleteChildProcedure(InfoEntity info, IdentifierRecord identification) {
        if (identification.type() != String.class) {
            throw new StatusNotSupportedException();
        }

        FileSystemEntryDefinition entry = ObjectUtil.transferFromByteArray(info.getContent());
        assert entry != null;

        String childInfoName = StringUtil.readFormBytes(identification.value());

        if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

            try {
                this.lockProcedure(info, LockType.WRITE);

                InfoRelationEntity infoRelation = infoRepository.getRelation(info, childInfoName);

                infoRepository.deleteRelation(infoRelation);
            } finally {
                this.unlockProcedure(info, LockType.WRITE);
            }
        } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
            File infoFolder = new File(StringUtil.readFormBytes(entry.getValue()));
            File infoRelationFolder = new File(infoFolder.getAbsolutePath() + "$Relations");

            if (!infoFolder.exists() || !infoFolder.isDirectory() || !infoRelationFolder.exists() || !infoRelationFolder.isDirectory()) {
                throw new StatusNotExistedException();
            }

            String[] childInfoNames = infoFolder.list((dir, name) -> childInfoName.equals(name));
            assert childInfoNames != null;

            if (childInfoNames.length == 0) {
                throw new StatusNotExistedException();
            }

            File childInfoFileFolder = new File(infoFolder.getAbsolutePath() + "/" + childInfoName);
            File childInfoRelationFile = new File(infoRelationFolder.getAbsolutePath() + "/" + childInfoName);

            if (childInfoFileFolder.isDirectory()) {
                File childInfoRelationFolder = new File(infoFolder.getAbsolutePath() + "/" + childInfoName + "$Relations");

                childInfoRelationFolder.delete();
            }

            childInfoFileFolder.delete();
            childInfoRelationFile.delete();
        }
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenRecord infoOpen) {
        return FileSystemFolderContentObject.class;
    }
}
