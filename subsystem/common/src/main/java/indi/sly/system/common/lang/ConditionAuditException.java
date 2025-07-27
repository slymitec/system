package indi.sly.system.common.lang;

import java.io.Serial;

public class ConditionAuditException extends AKernelException {
    @Serial
    private static final long serialVersionUID = 3373822921447386600L;

    public ConditionAuditException() {
        super(ConditionAuditException.class);
    }
}
