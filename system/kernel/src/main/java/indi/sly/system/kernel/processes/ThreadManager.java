package indi.sly.system.kernel.processes;

import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfiguration;
import indi.sly.system.kernel.core.enviroment.values.UserSpace;
import indi.sly.system.kernel.processes.prototypes.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.Stack;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadManager extends AManager {
    private ThreadFactory factory;

    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
            this.factory = this.coreManager.create(ThreadFactory.class);
            this.factory.init();
        } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
            KernelConfiguration kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

            this.create(kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID);
        }
    }

    @Override
    public void shutdown() {
    }

    public ThreadObject getCurrent() {
        UserSpace userSpace = this.coreManager.getUserSpace();
        Stack<ThreadObject> threads = userSpace.getThreads();

        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();

        if (ObjectUtil.isAnyNull(threads) || threads.isEmpty()) {
            throw new StatusNotExistedException();
        }

        ThreadObject thread = threads.peek();

        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.setDate(DateTimeType.ACCESS, nowDateTime);

        return thread;
    }

    public int size() {
        UserSpace userSpace = this.coreManager.getUserSpace();
        Stack<ThreadObject> threads = userSpace.getThreads();

        return ObjectUtil.isAnyNull(threads) ? 0 : threads.size();
    }

    public ThreadObject create(UUID processID) {
        ThreadBuilder threadBuilder = this.factory.createThread();

        ThreadObject thread = threadBuilder.create(processID);

        ThreadStatusObject threadStatus = thread.getStatus();
        threadStatus.running();

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessStatisticsObject processStatistics = process.getStatistics();
        processStatistics.addThreadCumulation(1);

        return thread;
    }

    public void end() {
        ThreadBuilder threadBuilder = this.factory.createThread();

        threadBuilder.end();
    }
}
