package indi.sly.system.common.exceptions;

public class StatusNotExistedException extends AKernelException {
	private static final long serialVersionUID = -9065065656431826101L;

	public StatusNotExistedException(long status, String message) {
		super(StatusNotExistedException.class, message);
	}

	public StatusNotExistedException() {
		super(StatusNotExistedException.class);
	}
}
