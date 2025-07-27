package indi.sly.system.common.lang;

@FunctionalInterface
public interface Provider<R> {
	R acquire();
}
