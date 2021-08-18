package indi.sly.system.kernel.security.values;

public interface PrivilegeType {
    long NULL = 0L;
    long FULL = -1L;

    long CORE_MODIFY_PRIVILEGES = PrivilegeType.FULL;
    long CORE_CACHE_OBJECT_IN_KERNEL_SPACE = 1L;
    long CORE_MODIFY_DATETIME = 1L << 1;
    long OBJECTS_ACCESS_INFOOBJECTS = 1L << 2;
    long PROCESSES_ADD_ROLES = 1L << 3;
    long PROCESSES_MODIFY_LIMITS = 1L << 4;
    long SECURITY_DO_WITH_ANY_ACCOUNT = 1L << 5;
    long SECURITY_MODIFY_ACCOUNT_AND_GROUP = 1L << 6;
    long SESSION_MODIFY_USER_SESSION = 1L << 7;
}
