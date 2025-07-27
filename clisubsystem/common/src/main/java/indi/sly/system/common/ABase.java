package indi.sly.system.common;

import indi.sly.system.common.lang.MethodScope;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.MethodScopeType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class ABase {
    public ABase() {
        super();
    }

    private Log _logger;

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    protected synchronized final Log logger() {
        if (ObjectUtil.isAnyNull(this._logger)) {
            this._logger = LogFactory.getLog(this.getClass());
        }

        return this._logger;
    }

    @Override
    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public Object clone() {
        throw new StatusNotSupportedException();
    }

    @Override
    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public String toString() {
        return ClassUtil.getAbbreviatedName(this.getClass(), 1) + "@" + Integer.toHexString(this.hashCode());
    }
}
