package indi.sly.system.common.lang;

import indi.sly.system.common.supports.ClassUtil;

import java.io.Serial;

public abstract class AKernelException extends RuntimeException {
    public AKernelException(Class<? extends AKernelException> type) {
        super(ClassUtil.getSimpleName(type));
        this.type = type;
    }

    protected final Class<? extends AKernelException> type;

    public Class<? extends AKernelException> getType() {
        return this.type;
    }
}
