package indi.sly.system.common.functions;

@FunctionalInterface
public interface Provider<R> {
	R acquire();
}
