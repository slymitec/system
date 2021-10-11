package indi.sly.system.services.job.values;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.services.job.lang.TaskInitializerRunMethodConsumer;

public class TaskInitializerRunSummaryDefinition extends ADefinition<TaskInitializerRunSummaryDefinition> {
    private TaskInitializerRunDefinition taskInitializerRun;

    public void setTaskInitializerRun(TaskInitializerRunDefinition taskInitializerRun) {
        this.taskInitializerRun = taskInitializerRun;
    }

    public TaskInitializerRunMethodConsumer getMethod() {
        if (ObjectUtil.isAnyNull(this.taskInitializerRun)) {
            throw new ConditionContextException();
        }

        return this.taskInitializerRun.getMethod();
    }

    public long getTransaction() {
        if (ObjectUtil.isAnyNull(this.taskInitializerRun)) {
            throw new ConditionContextException();
        }

        return this.taskInitializerRun.getTransaction();
    }
}
