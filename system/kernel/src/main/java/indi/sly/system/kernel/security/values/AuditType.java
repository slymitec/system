package indi.sly.system.kernel.security.values;

public interface AuditType {
    long NULL = 0L;

    long LISTCHILD_READDATA = 1L;
    long TRAVERSE_EXECUTE = 1L << 2;
    long CREATECHILD_WRITEDATA = 1L << 4;
    long TAKEONWERSHIP = 1L << 6;
    long READPROPERTIES = 1L << 8;
    long WRITEPROPERTIES = 1L << 10;
    long READPERMISSIONDESCRIPTOR = 1L << 12;
    long CHANGEPERMISSIONDESCRIPTOR = 1L << 14;
    long DELETECHILD = 1L << 16;
}
