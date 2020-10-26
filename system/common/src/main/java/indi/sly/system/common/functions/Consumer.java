package indi.sly.system.common.functions;

@FunctionalInterface
public interface Consumer<T> {
	void accept(T t);
}
