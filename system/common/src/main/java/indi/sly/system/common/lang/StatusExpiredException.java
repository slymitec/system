package indi.sly.system.common.lang;

public class StatusExpiredException extends AKernelException {
	private static final long serialVersionUID = -3513810206002805208L;

	public StatusExpiredException(long status, String message) {
		super(StatusExpiredException.class, message);
	}

	public StatusExpiredException() {
		super(StatusExpiredException.class);
	}
}
