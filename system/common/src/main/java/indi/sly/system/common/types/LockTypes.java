package indi.sly.system.common.types;

public interface LockTypes {
    long NONE = 0L;
    long WRITE = 1L; // Other Write prohibited
    long READ = 1L | 1L << 1; // Other Read prohibited
}
