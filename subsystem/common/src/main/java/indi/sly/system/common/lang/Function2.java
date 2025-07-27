package indi.sly.system.common.lang;

@FunctionalInterface
public interface Function2<R, T1, T2> {
	R apply(T1 t1, T2 t2);
}
