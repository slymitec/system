package indi.sly.system.kernel.security.values;

public interface PermissionType {
    long NULL = 0;

    /* Kernel: 0~31, User: 32~63 */

    long LISTCHILD_READDATA_ALLOW = 1L;
    long TRAVERSE_EXECUTE_ALLOW = 1L << 2;
    long CREATECHILD_WRITEDATA_ALLOW = 1L << 4;
    long TAKEONWERSHIP_ALLOW = 1L << 6;
    long READPROPERTIES_ALLOW = 1L << 8;
    long WRITEPROPERTIES_ALLOW = 1L << 10;
    long READPERMISSIONDESCRIPTOR_ALLOW = 1L << 12;
    long CHANGEPERMISSIONDESCRIPTOR_ALLOW = 1L << 14;
    long DELETECHILD_ALLOW = 1L << 16;

    long LISTCHILD_READDATA_DENY = LISTCHILD_READDATA_ALLOW << 1;
    long TRAVERSE_EXECUTE_DENY = TRAVERSE_EXECUTE_ALLOW << 1;
    long CREATECHILD_WRITEDATA_DENY = CREATECHILD_WRITEDATA_ALLOW << 1;
    long TAKEONWERSHIP_DENY = TAKEONWERSHIP_ALLOW << 1;
    long READPROPERTIES_DENY = READPROPERTIES_ALLOW << 1;
    long WRITEPROPERTIES_DENY = WRITEPROPERTIES_ALLOW << 1;
    long READPERMISSIONDESCRIPTOR_DENY = READPERMISSIONDESCRIPTOR_ALLOW << 1;
    long CHANGEPERMISSIONDESCRIPTOR_DENY = CHANGEPERMISSIONDESCRIPTOR_ALLOW << 1;
    long DELETECHILD_DENY = DELETECHILD_ALLOW << 1;

    long FULLCONTROL_ALLOW = 0x5555555555555555L;
    long FULLCONTROL_DENY = FULLCONTROL_ALLOW << 1;
}