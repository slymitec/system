package indi.sly.system.common.exceptions;

public class StatusAlreadyExistedException extends AKernelException {
	private static final long serialVersionUID = 7768599881918824792L;

	public StatusAlreadyExistedException(long status, String message) {
		super(StatusAlreadyExistedException.class, message);
	}

	public StatusAlreadyExistedException() {
		super(StatusAlreadyExistedException.class);
	}
}
