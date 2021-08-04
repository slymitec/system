package indi.sly.system.common.lang;

public class ConditionPermissionException extends AKernelException {
	private static final long serialVersionUID = -5042583987847060604L;

	public ConditionPermissionException(long status, String message) {
		super(ConditionPermissionException.class, message);
	}

	public ConditionPermissionException() {
		super(ConditionPermissionException.class);
	}
}
