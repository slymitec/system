package indi.sly.system.services.job.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.services.job.lang.JobInitializerRunMethodConsumer;

public class JobInitializerRunSummaryDefinition extends ADefinition<JobInitializerRunSummaryDefinition> {
    private JobInitializerRunMethodConsumer method;
    private long transaction;

    public JobInitializerRunMethodConsumer getMethod() {
        return this.method;
    }

    public void setMethod(JobInitializerRunMethodConsumer method) {
        this.method = method;
    }

    public long getTransaction() {
        return this.transaction;
    }

    public void setTransaction(long transaction) {
        this.transaction = transaction;
    }
}
