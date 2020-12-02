package indi.sly.system.kernel.processes.definitions;

import java.util.HashMap;
import java.util.Map;

public class ThreadStatisticsDefinition {
    public ThreadStatisticsDefinition() {
        this.date = new HashMap<>();
    }

    private Map<Long, Long> date;

    public Map<Long, Long> getDate() {
        return this.date;
    }
}
