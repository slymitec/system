package indi.sly.system.common.lang;

import java.io.Serial;

public class StatusNotSupportedException extends AKernelException {
	@Serial
	private static final long serialVersionUID = -4310330657148813269L;

	public StatusNotSupportedException() {
		super(StatusNotSupportedException.class);
	}
}
