package indi.sly.system.kernel.security.prototypes;

public interface PrivilegeTypes {
    long NULL = 0;

    long CORE_MODIFY_PRIVILEGES = -1L;
    long CORE_MODIFY_DATETIME = 1L;
    long OBJECTS_ACCESS_INFOOBJECTS = 1L << 2;
    long PROCESSES_RUN_APP_WITH_ANOTHER_ACCOUNT = 1L << 3;
    long SECURITY_MODIFY_ACCOUNT_AND_GROUP = 1L << 4;
}
