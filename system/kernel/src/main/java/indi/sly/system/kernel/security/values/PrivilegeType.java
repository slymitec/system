package indi.sly.system.kernel.security.values;

public interface PrivilegeType {
    long NULL = 0;
    long FULL = -1L;

    long CORE_MODIFY_PRIVILEGES = PrivilegeType.FULL;
    long CORE_MODIFY_DATETIME = 1L;
    long MEMORY_CACHE_MODIFYKERNELSPACECACHE = 1L << 2;
    long OBJECTS_ACCESS_INFOOBJECTS = 1L << 3;
    long PROCESSES_MODIFY_ANY_PROCESSES = 1L << 4;
    long PROCESSES_MODIFY_LIMITS = 1L << 5;
    long PROCESSES_ADD_ROLES = 1L << 6;
    long SECURITY_DO_WITH_ANY_ACCOUNT = 1L << 7;
    long SECURITY_MODIFY_ACCOUNT_AND_GROUP = 1L << 8;
    long SESSION_MODIFY_USERSESSION = 1L << 9;
}
