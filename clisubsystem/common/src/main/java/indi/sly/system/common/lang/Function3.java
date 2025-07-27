package indi.sly.system.common.lang;

@FunctionalInterface
public interface Function3<R, T1, T2, T3> {
	R apply(T1 t1, T2 t2, T3 t3);
}
