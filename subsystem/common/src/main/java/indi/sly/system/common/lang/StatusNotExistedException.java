package indi.sly.system.common.lang;

import java.io.Serial;

public class StatusNotExistedException extends AKernelException {
	@Serial
	private static final long serialVersionUID = -9065065656431826101L;

	public StatusNotExistedException() {
		super(StatusNotExistedException.class);
	}
}
