package indi.sly.system.kernel.files.instances.prototypes.wrappers;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.files.instances.prototypes.FileSystemFileContentObject;
import indi.sly.system.kernel.files.instances.values.FileSystemEntryDefinition;
import indi.sly.system.kernel.files.instances.values.FileSystemFilePropertyTypes;
import indi.sly.system.kernel.files.instances.values.FileSystemLocationType;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileSystemFileTypeInitializer extends AInfoTypeInitializer {
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
    public void getProcedure(InfoEntity info, IdentificationDefinition identification) {
        if (identification.getType() == String.class) {
            String childInfoName = StringUtil.readFormBytes(identification.getID());
            if (!childInfoName.equals(info.getName())) {
                info.setName(childInfoName);
            }
        }
    }

    @Override
    public Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        return FileSystemFileContentObject.class;
    }

    @Override
    public void writeContentProcedure(InfoEntity info, InfoOpenDefinition infoOpen, byte[] source) {
        Map<String, String> properties = this.readPropertiesProcedure(info, infoOpen);
        assert properties != null;

        String property;
        boolean isPropertiesModified = false;

        property = properties.getOrDefault(FileSystemFilePropertyTypes.HIDDEN, StringUtil.EMPTY);
        if (!ValueUtil.isAnyNullOrEmpty(property)) {
            throw new ConditionRefuseException();
        }
        property = properties.getOrDefault(FileSystemFilePropertyTypes.UNCHANGED, StringUtil.EMPTY);
        if (!ValueUtil.isAnyNullOrEmpty(property)) {
            properties.remove(FileSystemFilePropertyTypes.UNCHANGED);
            isPropertiesModified = true;
        }

        if (isPropertiesModified) {
            info.setProperties(ObjectUtil.transferToByteArray(properties));
        }

        super.writeContentProcedure(info, infoOpen, source);
    }
}
