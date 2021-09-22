package indi.sly.system.common.lang;

import java.io.Serial;

public class StatusOverflowException extends AKernelException {
    @Serial
    private static final long serialVersionUID = 209807174133597997L;

    public StatusOverflowException(long status, String message) {
        super(StatusOverflowException.class, message);
    }

    public StatusOverflowException() {
        super(StatusOverflowException.class);
    }
}
