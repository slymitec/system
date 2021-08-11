package indi.sly.system.services.job.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JobPointerDefinition extends ADefinition<JobPointerDefinition> {
    public JobPointerDefinition() {
        this.prototypes = new HashMap<>();
        this.date = new HashMap<>();
    }

    private UUID jobID;
    private int limit;
    private final Map<UUID, Class<? extends APrototype>> prototypes;
    private final Map<Long, Long> date;

    public UUID getJobID() {
        return this.jobID;
    }

    public void setJobID(UUID jobID) {
        this.jobID = jobID;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Map<UUID, Class<? extends APrototype>> getPrototypes() {
        return this.prototypes;
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }
}
