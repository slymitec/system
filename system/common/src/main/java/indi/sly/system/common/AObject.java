package indi.sly.system.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AObject {
    public AObject() {
        super();
        this.logger = LogFactory.getLog(this.getClass());
    }

    protected final Log logger;
}
