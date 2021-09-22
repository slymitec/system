package indi.sly.system.common.lang;

import java.io.Serial;

public class ConditionContextException extends AKernelException {
	@Serial
	private static final long serialVersionUID = 7924517995356757432L;

	public ConditionContextException(long status, String message) {
		super(ConditionContextException.class, message);
	}

	public ConditionContextException() {
		super(ConditionContextException.class);
	}
}
