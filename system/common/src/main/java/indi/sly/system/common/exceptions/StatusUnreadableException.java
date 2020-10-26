package indi.sly.system.common.exceptions;

public class StatusUnreadableException extends AKernelException {
	private static final long serialVersionUID = 6715518705653647642L;

	public StatusUnreadableException(long status, String message) {
		super(StatusUnreadableException.class, message);
	}

	public StatusUnreadableException() {
		super(StatusUnreadableException.class);
	}
}
