package indi.sly.system.common.exceptions;

public class StatusNotReadyException extends AKernelException {
	private static final long serialVersionUID = 664499902812713122L;

	public StatusNotReadyException(long status, String message) {
		super(StatusNotReadyException.class, message);
	}

	public StatusNotReadyException() {
		super(StatusNotReadyException.class);
	}
}
