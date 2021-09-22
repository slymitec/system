package indi.sly.system.common.lang;

import java.io.Serial;

public class ConditionPermissionException extends AKernelException {
	@Serial
	private static final long serialVersionUID = -5042583987847060604L;

	public ConditionPermissionException(long status, String message) {
		super(ConditionPermissionException.class, message);
	}

	public ConditionPermissionException() {
		super(ConditionPermissionException.class);
	}
}
