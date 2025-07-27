package indi.sly.system.common.lang;

import java.io.Serial;

public class StatusAlreadyExistedException extends AKernelException {
	@Serial
	private static final long serialVersionUID = 7768599881918824792L;

	public StatusAlreadyExistedException() {
		super(StatusAlreadyExistedException.class);
	}
}
