package indi.sly.system.kernel.core.enviroment;

import java.util.UUID;

import indi.sly.system.common.utility.UUIDUtils;

public class KernelConfiguration {
    public UUID MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID = UUIDUtils.getEmpty();

    public UUID OBJECTS_PROTOTYPE_ROOT_ID = UUIDUtils.getEmpty();
    public UUID OBJECTS_TYPES_INSTANCE_FOLDER_ID = UUIDUtils.getEmpty();
    public String OBJECTS_TYPES_INSTANCE_FOLDER_NAME = "Folder";
    public UUID OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID = UUIDUtils.getEmpty();
    public String OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_NAME = "NamelessFolder";

    public UUID PROCESSES_SHADOW_SHADOWKERNEMODE_ID = UUIDUtils.getEmpty();

}
