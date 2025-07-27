package indi.sly.system.common.lang;

public interface IDeepCloneCapable<T> extends Cloneable {
	T deepClone();
}
