package indi.sly.system.kernel.core.enviroment;

import java.util.UUID;

import indi.sly.system.common.utility.UUIDUtils;

public class KernelConfiguration {
    public final UUID MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID
            = UUIDUtils.getFormLongs(8953595201274071621L, -4864294792184025833L);
    public final long MEMORY_CACHES_USERSPACE_INFOOBJECT_EXPIRED_TIME = 1048576l;

    public final UUID OBJECTS_PROTOTYPE_ROOT_ID
            = UUIDUtils.getFormLongs(-6865430042473446686L, -4838027132975818569L);
    public final UUID OBJECTS_TYPES_INSTANCE_FOLDER_ID
            = UUIDUtils.getFormLongs(-5711673899133549992L, -6470820587108630906L);
    public final String OBJECTS_TYPES_INSTANCE_FOLDER_NAME = "Folder";
    public final UUID OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID
            = UUIDUtils.getFormLongs(8436994597612111739L, -6974556479988790262L);
    public final String OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_NAME = "NamelessFolder";

    public UUID PROCESSES_SHADOW_SHADOWKERNEMODE_ID
            = UUIDUtils.getFormLongs(4327463367325207662L, -5421948031898080267L);
}
