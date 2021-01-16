package indi.sly.system.kernel.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class AObject extends Object {
    public AObject() {
        super();
        this.logger = LogFactory.getLog(this.getClass());
    }

    protected Log logger;
}
