package indi.sly.system.common.values;

public interface DateTimeType {
    long ACCESS = 1L;
    long CREATE = 1L << 1;
    long MODIFIED = 1L << 2;
    long EXPIRED = 1L << 3;
}
