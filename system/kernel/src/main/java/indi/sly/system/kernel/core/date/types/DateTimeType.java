package indi.sly.system.kernel.core.date.types;

public interface DateTimeType {
    long ACCESS = 1L;
    long CREATE = 1L << 1;

    long MODIFIED = 1L << 2;
}
