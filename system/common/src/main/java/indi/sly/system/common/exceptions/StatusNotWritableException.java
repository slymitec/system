package indi.sly.system.common.exceptions;

public class StatusNotWritableException extends AKernelException {
	private static final long serialVersionUID = -1103689550677214892L;

	public StatusNotWritableException(long status, String message) {
		super(StatusNotWritableException.class, message);
	}

	public StatusNotWritableException() {
		super(StatusNotWritableException.class);
	}
}
