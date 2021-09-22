package indi.sly.system.common.lang;

import java.io.Serial;

public class StatusNotWritableException extends AKernelException {
    @Serial
    private static final long serialVersionUID = -1103689550677214892L;

    public StatusNotWritableException(long status, String message) {
        super(StatusNotWritableException.class, message);
    }

    public StatusNotWritableException() {
        super(StatusNotWritableException.class);
    }
}
