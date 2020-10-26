package indi.sly.system.common.exceptions;

public class ConditionRefuseException extends AKernelException {
	private static final long serialVersionUID = -6395626520348248125L;

	public ConditionRefuseException(long status, String message) {
		super(ConditionRefuseException.class, message);
	}

	public ConditionRefuseException() {
		super(ConditionRefuseException.class);
	}
}
