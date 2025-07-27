package indi.sly.system.common.lang;

import java.io.Serial;

public class StatusUnreadableException extends AKernelException {
    @Serial
    private static final long serialVersionUID = 6715518705653647642L;

    public StatusUnreadableException() {
        super(StatusUnreadableException.class);
    }
}
