package indi.sly.system.common.lang;

import indi.sly.system.common.supports.StringUtil;

public abstract class AKernelException extends RuntimeException {
    private static final long serialVersionUID = 3353990103481301713L;

    public AKernelException(Class<? extends AKernelException> type, String message) {
        super(type.getName() + (message != null ? " (" + message + ")" : StringUtil.EMPTY));
        this.type = type;
    }

    public AKernelException(Class<? extends AKernelException> type) {
        this(type, null);
    }

    protected final Class<? extends AKernelException> type;

    public Class<? extends AKernelException> getType() {
        return this.type;
    }
}
