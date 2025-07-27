package indi.sly.system.common.lang;

import java.io.Serial;

public class StatusNotReadyException extends AKernelException {
	@Serial
	private static final long serialVersionUID = 664499902812713122L;

	public StatusNotReadyException() {
		super(StatusNotReadyException.class);
	}
}
