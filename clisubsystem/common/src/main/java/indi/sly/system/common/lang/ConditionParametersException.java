package indi.sly.system.common.lang;

import java.io.Serial;

public class ConditionParametersException extends AKernelException {
    @Serial
    private static final long serialVersionUID = 4006294428736602907L;

    public ConditionParametersException() {
        super(ConditionParametersException.class);
    }
}
