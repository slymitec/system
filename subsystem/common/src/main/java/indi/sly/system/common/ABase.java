package indi.sly.system.common;

import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class ABase {
    public ABase() {
        super();
    }

    private Log _logger;

    protected synchronized final Log logger() {
        if (ObjectUtil.isAnyNull(this._logger)) {
            this._logger = LogFactory.getLog(this.getClass());
        }

        return this._logger;
    }

    @Override
    public Object clone() {
        throw new StatusNotSupportedException();
    }

    @Override
    public String toString() {
        return ClassUtil.getAbbreviatedName(this.getClass(), 1) + "@" + Integer.toHexString(this.hashCode());
    }
}
