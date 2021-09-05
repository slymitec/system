package indi.sly.system.common;

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
}
