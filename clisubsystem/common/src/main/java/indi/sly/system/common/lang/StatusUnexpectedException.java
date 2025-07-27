package indi.sly.system.common.lang;

import java.io.Serial;

public class StatusUnexpectedException extends AKernelException {
	@Serial
	private static final long serialVersionUID = -584392078370667917L;

	public StatusUnexpectedException() {
		super(StatusUnexpectedException.class);
	}
}
