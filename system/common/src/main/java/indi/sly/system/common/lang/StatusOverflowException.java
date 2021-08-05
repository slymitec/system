package indi.sly.system.common.lang;

public class StatusOverflowException extends AKernelException {
    private static final long serialVersionUID = 209807174133597997L;

    public StatusOverflowException(long status, String message) {
        super(StatusOverflowException.class, message);
    }

    public StatusOverflowException() {
        super(StatusOverflowException.class);
    }
}
