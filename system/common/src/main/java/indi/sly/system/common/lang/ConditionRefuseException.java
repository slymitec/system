package indi.sly.system.common.lang;

import java.io.Serial;

public class ConditionRefuseException extends AKernelException {
    @Serial
    private static final long serialVersionUID = -6395626520348248125L;

    public ConditionRefuseException(long status, String message) {
        super(ConditionRefuseException.class, message);
    }

    public ConditionRefuseException() {
        super(ConditionRefuseException.class);
    }
}
