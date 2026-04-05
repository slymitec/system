package indi.sly.system.common;

import indi.sly.system.common.lang.StatusNotSupportedException;

public class ANull {
    public ANull() {
        super();
    }

    @Override
    public boolean equals(Object obj) {
        throw new StatusNotSupportedException();
    }

    @Override
    public int hashCode() {
        throw new StatusNotSupportedException();
    }

    public Object clone() {
        throw new StatusNotSupportedException();
    }

    public String toString() {
        throw new StatusNotSupportedException();
    }
}
