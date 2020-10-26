package indi.sly.system.common.exceptions;

import indi.sly.system.common.utility.StringUtils;

public abstract class AKernelException extends RuntimeException {
	private static final long serialVersionUID = 3353990103481301713L;

	protected final Class<? extends AKernelException> type;

	public AKernelException(Class<? extends AKernelException> type, String message) {
		super(type.getName() + (message != null ? " (" + message + ")" : StringUtils.EMPTY));
		this.type = type;
	}

	public AKernelException(Class<? extends AKernelException> type) {
		this(type, null);
	}

	public Class<? extends AKernelException> getType() {
		return this.type;
	}
	
	/*
	ArithmeticException
	ArrayIndexOutOfBoundsException
	ArrayStoreException
	ClassCastException
	ClassNotFoundException
	CloneNotSupportedException
	EnumConstantNotPresentException
	ExceptionInInitializerError
	IllegalAccessException
	IllegalArgumentException
	IllegalMonitorStateException
	IllegalStateException
	IllegalThreadStateException
	IndexOutOfBoundsException
	InstantiationException
	InterruptedException
	NegativeArraySizeException
	NoSuchFieldException
	NoSuchMethodException
	NullPointerException
	NumberFormatException
	ReflectiveOperationException
	SecurityException
	StringIndexOutOfBoundsException
	TypeNotPresentException
	UnsupportedOperationException
	*/
}
