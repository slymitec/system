package indi.sly.system.common.lang;

import indi.sly.system.common.supports.ClassUtil;

public abstract class ASystemException extends RuntimeException {
    public ASystemException(Class<? extends ASystemException> type) {
        super(ClassUtil.getSimpleName(type));
        this.type = type;
    }

    public ASystemException(Class<? extends ASystemException> type, ASystemException cause) {
        super(ClassUtil.getSimpleName(type), cause);
        this.type = type;
    }

    protected final Class<? extends ASystemException> type;

    public Class<? extends ASystemException> getType() {
        return this.type;
    }
}
