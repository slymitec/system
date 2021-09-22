package indi.sly.system.common.lang;

import java.io.Serial;

public class StatusExpiredException extends AKernelException {
	@Serial
	private static final long serialVersionUID = -3513810206002805208L;

	public StatusExpiredException(long status, String message) {
		super(StatusExpiredException.class, message);
	}

	public StatusExpiredException() {
		super(StatusExpiredException.class);
	}
}
