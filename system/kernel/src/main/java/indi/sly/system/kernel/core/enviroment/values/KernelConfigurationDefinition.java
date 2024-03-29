package indi.sly.system.kernel.core.enviroment.values;

import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.processes.values.ProcessTokenLimitType;

import java.util.Map;
import java.util.UUID;

public class KernelConfigurationDefinition extends ADefinition<KernelSpaceDefinition> {
    public final long CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT = 16L;

    public final UUID FILES_TYPES_INSTANCE_FILE_ID
            = UUIDUtil.getFormLongs(5141458603474635654L, -6744682139694385633L);
    public final String FILES_TYPES_INSTANCE_FILE_NAME = "File";
    public final UUID FILES_TYPES_INSTANCE_FOLDER_ID
            = UUIDUtil.getFormLongs(1916803478875881556L, -6711235423546474736L);
    public final String FILES_TYPES_INSTANCE_FOLDER_NAME = "Folder";

    public final UUID MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID
            = UUIDUtil.getFormLongs(8953595201274071621L, -4864294792184025833L);

    public final UUID OBJECTS_PROTOTYPE_ROOT_ID
            = UUIDUtil.getFormLongs(-6865430042473446686L, -4838027132975818569L);
    public final UUID OBJECTS_TYPES_INSTANCE_FOLDER_ID
            = UUIDUtil.getFormLongs(-5711673899133549992L, -6470820587108630906L);
    public final String OBJECTS_TYPES_INSTANCE_FOLDER_NAME = "Folder";
    public final UUID OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID
            = UUIDUtil.getFormLongs(8436994597612111739L, -6974556479988790262L);
    public final String OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_NAME = "NamelessFolder";

    public final UUID PROCESSES_COMMUNICATION_INSTANCE_PORT_ID
            = UUIDUtil.getFormLongs(-377164119524884257L, -8458654780268794138L);
    public final String PROCESSES_COMMUNICATION_INSTANCE_PORT_NAME = "Port";
    public final UUID PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_ID
            = UUIDUtil.getFormLongs(1995202583338893550L, -6724752038670449403L);
    public final String PROCESSES_COMMUNICATION_INSTANCE_SIGNAL_NAME = "Signal";
    public final UUID PROCESSES_SESSION_INSTANCE_ID
            = UUIDUtil.getFormLongs(-6729493064232579129L, -6576183935232554309L);
    public final String PROCESSES_SESSION_INSTANCE_NAME = "Session";
    public final UUID PROCESSES_PROTOTYPE_SYSTEM_ID
            = UUIDUtil.getFormLongs(-7191269949502635218L, -6103845440396733665L);
    public final Map<Long, Integer> PROCESSES_TOKEN_DEFAULT_LIMIT = Map.of(
            ProcessTokenLimitType.INDEX_MAX, 256,
            ProcessTokenLimitType.PORT_COUNT_MAX, 4,
            ProcessTokenLimitType.PORT_LENGTH_MAX, 4096,
            ProcessTokenLimitType.SHARED_LENGTH_MAX, 4096,
            ProcessTokenLimitType.SIGNAL_LENGTH_MAX, 256,
            ProcessTokenLimitType.JOB_PROTOTYPE_CACHES_MAX, 8
    );
    public final Map<Long, Integer> PROCESSES_TOKEN_FULL_LIMIT = Map.of(
            ProcessTokenLimitType.INDEX_MAX, Integer.MAX_VALUE,
            ProcessTokenLimitType.PORT_COUNT_MAX, Integer.MAX_VALUE,
            ProcessTokenLimitType.PORT_LENGTH_MAX, Integer.MAX_VALUE,
            ProcessTokenLimitType.SHARED_LENGTH_MAX, Integer.MAX_VALUE,
            ProcessTokenLimitType.SIGNAL_LENGTH_MAX, Integer.MAX_VALUE,
            ProcessTokenLimitType.JOB_PROTOTYPE_CACHES_MAX, Integer.MAX_VALUE
    );

    public final long SECURITY_ACCOUNT_AUTHORIZATION_EXPIRED_TIME = 65536L;
    public final long SECURITY_ACCOUNT_SESSION_DEFAULT_LIMIT = 4L;
    public final UUID SECURITY_ACCOUNT_SYSTEM_ID
            = UUIDUtil.getFormLongs(5081613797628857462L, -8788797059387809235L);
    public final String SECURITY_ACCOUNT_SYSTEM_NAME = "System";
    public final UUID SECURITY_GROUP_ADMINISTRATORS_ID
            = UUIDUtil.getFormLongs(-8601383103213975109L, -7868140629624229647L);
    public final UUID SECURITY_GROUP_SYSTEMS_ID
            = UUIDUtil.getFormLongs(-6207449898682267310L, -6401172295543185544L);
    public final UUID SECURITY_GROUP_USERS_ID
            = UUIDUtil.getFormLongs(-7945695043641654534L, -7688533197792195691L);
    public final UUID SECURITY_INSTANCE_AUDIT_ID
            = UUIDUtil.getFormLongs(3236896532833520303L, -7376471019645273123L);
    public final String SECURITY_INSTANCE_AUDIT_NAME = "Audit";
    public final UUID SECURITY_ROLE_API_ID
            = UUIDUtil.getFormLongs(8490229373355246097L, -7508645069123139812L);
    public final UUID SECURITY_ROLE_BATCHES_ID
            = UUIDUtil.getFormLongs(-3421149186667757009L, -6553408691387866191L);
    public final UUID SECURITY_ROLE_CLI_ID
            = UUIDUtil.getFormLongs(-6728325378663890637L, -7846507187846598185L);
    public final UUID SECURITY_ROLE_EXECUTABLE_ID
            = UUIDUtil.getFormLongs(1419971759074265790L, -5615446261138463344L);
    public final UUID SECURITY_ROLE_EMPTY_PASSWORD_ID
            = UUIDUtil.getFormLongs(1951212975570568104L, -8968775932760482565L);
    public final UUID SECURITY_ROLE_GUI_ID
            = UUIDUtil.getFormLongs(-1619619213472611191L, -4997309008069038213L);

    //    public final UUID CORE_ENVIRONMENT_USER_SPACE_EXTENSION_SERVICE
    //            = UUIDUtil.getFormLongs(1978793907977537155L, -7186786965693311562L);
    // public final UUID * = UUIDUtil.getFormLongs(-623865349106545073L, -6997297376516953324L);
    // public final UUID * = UUIDUtil.getFormLongs(-3687831299068770016L, -5951841443637097351L);
    // public final UUID * = UUIDUtil.getFormLongs(-1889690034818560723L, -6740428121948687006L);
    // public final UUID * = UUIDUtil.getFormLongs(-6717814366200446673L, -7746254531580977695L);
    // public final UUID * = UUIDUtil.getFormLongs(4921857748973800422L, -8509547467847298937L);
    // public final UUID * = UUIDUtil.getFormLongs(-3593723732334522685L, -8403161936913422272L);
    // public final UUID * = UUIDUtil.getFormLongs(664567456178587062L, -7473974241703046221L);
    // public final UUID * = UUIDUtil.getFormLongs(6777827075164359237L, -6593606754970534053L);
    // public final UUID * = UUIDUtil.getFormLongs(-8145858833867586780L, -8296701644252688692L);
    // public final UUID * = UUIDUtil.getFormLongs(-5587006296190270038L, -8092516312838105945L);
    // public final UUID * = UUIDUtil.getFormLongs(-5122125579855835153L, -5714034909898196951L);
    // public final UUID * = UUIDUtil.getFormLongs(-6975266409795927796L, -5673552816359443899L);
    // public final UUID * = UUIDUtil.getFormLongs(-1949419174447854865L, -7648010831023358931L);
}
