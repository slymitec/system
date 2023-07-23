package indi.sly.system.common.lang;

public abstract class AKernelException extends RuntimeException {
    private static final long serialVersionUID = 3353990103481301713L;

    public AKernelException(Class<? extends AKernelException> type) {
        super(type.getName());
        this.type = type;
    }

    protected final Class<? extends AKernelException> type;

    public Class<? extends AKernelException> getType() {
        return this.type;
    }
}
