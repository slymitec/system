package indi.sly.system.common.lang;

@FunctionalInterface
public interface Function1<R, T1> {
    R apply(T1 t1) throws AKernelException;
}
