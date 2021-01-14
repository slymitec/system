package indi.sly.system.common.lang;

public class StatusUnexpectedException extends AKernelException {
	private static final long serialVersionUID = -584392078370667917L;

	public StatusUnexpectedException(long status, String message) {
		super(StatusUnexpectedException.class, message);
	}

	public StatusUnexpectedException() {
		super(StatusUnexpectedException.class);
	}
}
