package indi.sly.system.services.jobs.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.services.jobs.lang.TaskInitializerRunMethodConsumer;

public class TaskInitializerRunDefinition extends ADefinition<TaskInitializerRunDefinition> {
    private TaskInitializerRunMethodConsumer method;
    private long transaction;

    public TaskInitializerRunMethodConsumer getMethod() {
        return this.method;
    }

    public void setMethod(TaskInitializerRunMethodConsumer method) {
        this.method = method;
    }

    public long getTransaction() {
        return this.transaction;
    }

    public void setTransaction(long transaction) {
        this.transaction = transaction;
    }
}
