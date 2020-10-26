package indi.sly.system.common.exceptions;

public class ConditionPermissionsException extends AKernelException {
	private static final long serialVersionUID = -5042583987847060604L;

	public ConditionPermissionsException(long status, String message) {
		super(ConditionPermissionsException.class, message);
	}

	public ConditionPermissionsException() {
		super(ConditionPermissionsException.class);
	}
}
