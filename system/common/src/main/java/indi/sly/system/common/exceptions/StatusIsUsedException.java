package indi.sly.system.common.exceptions;

public class StatusIsUsedException extends AKernelException {
	private static final long serialVersionUID = 4478352651654516365L;

	public StatusIsUsedException(long status, String message) {
		super(StatusIsUsedException.class, message);
	}

	public StatusIsUsedException() {
		super(StatusIsUsedException.class);
	}
}
