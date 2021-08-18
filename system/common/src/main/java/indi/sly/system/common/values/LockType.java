package indi.sly.system.common.values;

public interface LockType {
    long NONE = 0L;
    long WRITE = 1L; // Other Write prohibited
    long READ = 2L; // Other Read prohibited
}
