package indi.sly.system.kernel.core.enviroment.values;

import java.util.Map;
import java.util.UUID;

import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.processes.values.ProcessTokenLimitType;

public class KernelConfigurationDefinition extends ADefinition<KernelSpaceDefinition> {
    public final UUID MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID
            = UUIDUtil.getFormLongs(8953595201274071621l, -4864294792184025833l);
    public final long MEMORY_CACHES_USERSPACE_INFOOBJECT_EXPIRED_TIME = 1048576l;

    public final UUID OBJECTS_PROTOTYPE_ROOT_ID
            = UUIDUtil.getFormLongs(-6865430042473446686l, -4838027132975818569l);
    public final UUID OBJECTS_TYPES_INSTANCE_FOLDER_ID
            = UUIDUtil.getFormLongs(-5711673899133549992l, -6470820587108630906l);
    public final String OBJECTS_TYPES_INSTANCE_FOLDER_NAME = "Folder";
    public final UUID OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID
            = UUIDUtil.getFormLongs(8436994597612111739l, -6974556479988790262l);
    public final String OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_NAME = "NamelessFolder";

    public final UUID PROCESSES_COMMUNICATION_INSTANCE_PIPE_ID
            = UUIDUtil.getFormLongs(1916803478875881556l, -6711235423546474736l);
    public final String PROCESSES_COMMUNICATION_INSTANCE_PIPE_NAME = "Pipe";
    public final UUID PROCESSES_COMMUNICATION_INSTANCE_PORT_ID
            = UUIDUtil.getFormLongs(-377164119524884257l, -8458654780268794138l);
    public final String PROCESSES_COMMUNICATION_INSTANCE_PORT_NAME = "Port";
    public final UUID PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_ID
            = UUIDUtil.getFormLongs(1995202583338893550l, -6724752038670449403l);
    public final String PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_NAME = "Signal";
    public final UUID PROCESSES_COMMUNICATION_INSTANCE_SESSION_ID
            = UUIDUtil.getFormLongs(-6729493064232579129l, -6576183935232554309l);
    public final String PROCESSES_COMMUNICATION_INSTANCE_SESSION_NAME = "Session";
    public final Map<Long, Integer> PROCESSES_TOKEN_DEFAULT_LIMIT = Map.of(
            ProcessTokenLimitType.HANDLE_MAX, 256,
            ProcessTokenLimitType.SHARED_LENGTH_MAX, 4096,
            ProcessTokenLimitType.PORT_COUNT_MAX, 4,
            ProcessTokenLimitType.PORT_LENGTH_MAX, 4096,
            ProcessTokenLimitType.SIGNAL_LENGTH_MAX, 256);

    public final long SECURITY_ACCOUNT_AUTHORIZATION_EXPIRED_TIME = 65536l;
    public final UUID SECURITY_ACCOUNT_SYSTEM_ID
            = UUIDUtil.getFormLongs(5081613797628857462l, -8788797059387809235l);
    public final UUID SECURITY_GROUP_ADMINISTRATORS_ID
            = UUIDUtil.getFormLongs(-8601383103213975109l, -7868140629624229647l);
    public final UUID SECURITY_GROUP_SYSTEMS_ID
            = UUIDUtil.getFormLongs(-6207449898682267310l, -6401172295543185544l);
    public final UUID SECURITY_GROUP_USERS_ID
            = UUIDUtil.getFormLongs(-7945695043641654534l, -7688533197792195691l);
}
