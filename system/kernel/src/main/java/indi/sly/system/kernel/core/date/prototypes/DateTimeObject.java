package indi.sly.system.kernel.core.date.prototypes;

import java.util.Date;

import indi.sly.system.kernel.core.prototypes.ACoreObject;

public class DateTimeObject extends ACoreObject {
    public long getCurrentDateTime() {
        return System.currentTimeMillis();
    }
}
