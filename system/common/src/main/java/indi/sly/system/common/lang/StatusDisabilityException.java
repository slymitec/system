package indi.sly.system.common.lang;

public class StatusDisabilityException extends AKernelException {
	private static final long serialVersionUID = 1135676122457146736L;

	public StatusDisabilityException(long status, String message) {
		super(StatusDisabilityException.class, message);
	}

	public StatusDisabilityException() {
		super(StatusDisabilityException.class);
	}
}
