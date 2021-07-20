package indi.sly.system.kernel.security.values;

public interface AccessControlScopeType {
    long THIS = 1L; //0
    long CHILD_HAS_CHILD = 1L << 1; //1
    long CHILD_HAS_NOT_CHILD = 1L << 3; //3
    long CHILD = CHILD_HAS_CHILD | CHILD_HAS_NOT_CHILD; //1,3
    long HIERARCHICAL_HAS_CHILD = 1L << 2 | CHILD_HAS_CHILD; //1,2
    long HIERARCHICAL_HAS_NOT_CHILD = 1L << 4 | CHILD_HAS_CHILD | CHILD_HAS_NOT_CHILD; //1,3,4
    long HIERARCHICAL = HIERARCHICAL_HAS_CHILD | HIERARCHICAL_HAS_NOT_CHILD; //1,2,3,4
    long ALL = THIS | CHILD | HIERARCHICAL; //0,1,2,3,4
}
