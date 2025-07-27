package indi.sly.system.common.lang;

import java.io.Serial;

public class StatusAlreadyFinishedException extends AKernelException {
	@Serial
	private static final long serialVersionUID = -5900655401527888856L;

	public StatusAlreadyFinishedException() {
		super(StatusAlreadyFinishedException.class);
	}
}
