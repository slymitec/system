package indi.sly.system.common.functions;

import indi.sly.system.common.exceptions.AKernelException;

@FunctionalInterface
public interface Function<R, T1> {
	R apply(T1 t1) throws AKernelException;
}
