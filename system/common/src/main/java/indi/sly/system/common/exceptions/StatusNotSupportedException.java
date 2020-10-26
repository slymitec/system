package indi.sly.system.common.exceptions;

public class StatusNotSupportedException extends AKernelException {
	private static final long serialVersionUID = -4310330657148813269L;

	public StatusNotSupportedException(long status, String message) {
		super(StatusNotSupportedException.class, message);
	}

	public StatusNotSupportedException() {
		super(StatusNotSupportedException.class);
	}
}
