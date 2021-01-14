package indi.sly.system.common.lang;

public class StatusAlreadyFinishedException extends AKernelException {
	private static final long serialVersionUID = -5900655401527888856L;

	public StatusAlreadyFinishedException(long status, String message) {
		super(StatusAlreadyFinishedException.class, message);
	}

	public StatusAlreadyFinishedException() {
		super(StatusAlreadyFinishedException.class);
	}
}
