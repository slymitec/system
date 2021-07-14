package indi.sly.system.kernel.security.values;

public interface AuditTypes {
    long NULL = 0L;

    long LISTCHILD_READDATA = 1;
    long TRAVERSE_EXECUTE = 1 << 2L;
    long CREATECHILD_WRITEDATA = 1 << 4L;
    long TAKEONWERSHIP = 1 << 6L;
    long READPROPERTIES = 1 << 8L;
    long WRITEPROPERTIES = 1 << 10L;
    long READPERMISSIONDESCRIPTOR = 1 << 12L;
    long CHANGEPERMISSIONDESCRIPTOR = 1 << 14L;
    long DELETECHILD = 1 << 16L;
}
