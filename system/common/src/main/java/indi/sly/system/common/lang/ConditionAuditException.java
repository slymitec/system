package indi.sly.system.common.lang;

@Deprecated
public class ConditionAuditException extends AKernelException {
	private static final long serialVersionUID = 3373822921447386600L;

	public ConditionAuditException(long status, String message) {
		super(ConditionAuditException.class, message);
	}

	public ConditionAuditException() {
		super(ConditionAuditException.class);
	}
}
