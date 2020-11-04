package indi.sly.system.kernel.core.date;

import java.util.Date;

import indi.sly.system.kernel.core.prototypes.ACoreObject;

public class DateTimeObject extends ACoreObject {
    public Date getCurrentDateTime() {
        return new Date();
    }
}
