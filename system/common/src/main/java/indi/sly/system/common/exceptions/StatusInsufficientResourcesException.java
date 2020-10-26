package indi.sly.system.common.exceptions;

public class StatusInsufficientResourcesException extends AKernelException {
	private static final long serialVersionUID = 113351190782928516L;

	public StatusInsufficientResourcesException(long status, String message) {
		super(StatusInsufficientResourcesException.class, message);
	}

	public StatusInsufficientResourcesException() {
		super(StatusInsufficientResourcesException.class);
	}
}
