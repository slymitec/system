package indi.sly.system.kernel.processes.prototypes;

import java.util.HashMap;
import java.util.Map;

public class ThreadStatusDefinition {
    public ThreadStatusDefinition() {
        this.date = new HashMap<>();
    }

    private Map<Long, Long> date;

    public Map<Long, Long> getDate() {
        return this.date;
    }
}
