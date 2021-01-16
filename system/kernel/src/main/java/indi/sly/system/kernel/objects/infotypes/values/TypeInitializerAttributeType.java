package indi.sly.system.kernel.objects.infotypes.values;

public interface TypeInitializerAttributeType {
    long CAN_BE_SENT_AND_INHERITED = 1L << 1;
    long CAN_BE_SHARED_READ = 1L << 2;
    long CAN_BE_SHARED_WRITE = CAN_BE_SHARED_READ | 1L << 3;
    long CHILD_IS_NAMELESS = 1L << 4;
    long DONOT_USE_TYPE_COUNT = 1L << 5;
    long HAS_AUDIT = 1L << 6;
    long HAS_CHILD = 1L << 7;
    long HAS_CONTENT = 1L << 8;
    long HAS_PERMISSION = 1L << 9;
    long HAS_PROPERTIES = 1L << 10;
    long TEMPORARY = 1L << 11; // Could not HAS_CHILD
}
