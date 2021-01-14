package indi.sly.system.common.lang;

public class ConditionParametersException extends AKernelException {
    private static final long serialVersionUID = 4006294428736602907L;

    public ConditionParametersException(long status, String message) {
        super(ConditionParametersException.class, message);
    }

    public ConditionParametersException() {
        super(ConditionParametersException.class);
    }
}
