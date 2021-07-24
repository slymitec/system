package indi.sly.system.kernel.objects.infotypes.values;

public interface TypeInitializerAttributeType {
    long CAN_BE_EXECUTED = 1L << 1;
    long CAN_BE_SENT_AND_INHERITED = 1L << 2;
    long CAN_BE_SHARED_READ = 1L << 3;
    long CAN_BE_SHARED_WRITTEN = CAN_BE_SHARED_READ | 1L << 4;
    long CHILD_IS_NAMELESS = 1L << 5;
    long DO_NOT_USE_TYPE_COUNT = 1L << 6;
    long HAS_AUDIT = 1L << 7;
    long HAS_CHILD = 1L << 8;
    long HAS_CONTENT = 1L << 9;
    long HAS_PERMISSION = 1L << 10;
    long HAS_PROPERTIES = 1L << 11;
    long TEMPORARY = 1L << 12; // Could not HAS_CHILD
}
