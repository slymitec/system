package indi.sly.system.common.support;

public interface IDeepCloneable<T> extends Cloneable {
	T deepClone();
}
