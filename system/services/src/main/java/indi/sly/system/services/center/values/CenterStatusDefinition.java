package indi.sly.system.services.center.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;

public class CenterStatusDefinition extends ADefinition<CenterStatusDefinition> {
    public CenterStatusDefinition() {
        this.date = new HashMap<>();
    }

    private final Map<Long, Long> date;
    private long runtime;

    public Map<Long, Long> getDate() {
        return this.date;
    }

    public long getRuntime() {
        return this.runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }
}
