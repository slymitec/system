package indi.sly.system.common.exceptions;

public class ConditionContextException extends AKernelException {
	private static final long serialVersionUID = 7924517995356757432L;

	public ConditionContextException(long status, String message) {
		super(ConditionContextException.class, message);
	}

	public ConditionContextException() {
		super(ConditionContextException.class);
	}
}
