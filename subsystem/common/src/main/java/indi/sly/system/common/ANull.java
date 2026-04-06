package indi.sly.system.common;

import indi.sly.system.common.lang.StatusNotSupportedException;

public abstract class ANull {
    public ANull() {
        super();
    }

    @Override
    public final boolean equals(Object obj) {
        throw new StatusNotSupportedException();
    }

    @Override
    public final int hashCode() {
        throw new StatusNotSupportedException();
    }

    public final Object clone() {
        throw new StatusNotSupportedException();
    }

    public final String toString() {
        throw new StatusNotSupportedException();
    }
}
