package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.DateTimeType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.environment.containers.UserSpace;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.kernel.processes.values.ThreadContextType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Deque;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadBuilder extends ABuilder {
    @SuppressWarnings("NullAway.Init")
    protected ThreadFactory factory;

    public ThreadObject create(UUID processId) {
        UserSpace userSpace = this.coreManager.getUserSpace();

        if(ObjectUtil.isNull(userSpace)){
            throw new StatusNotReadyException();
        }

        Deque<ThreadObject> threads = userSpace.getThreads();

        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();

        ThreadObject thread = this.factory.buildThread(processId);

        ThreadStatusObject threadStatus = thread.getStatus();
        threadStatus.initialize();

        ThreadContextObject context = thread.getContext();
        if (threads.isEmpty()) {
            context.setType(ThreadContextType.USER);
        } else {
            context.setType(ThreadContextType.DAEMON);
        }

        ThreadStatisticsObject threadStatistics = thread.getStatistics();
        threadStatistics.setDate(DateTimeType.CREATE, nowDateTime);

        threads.push(thread);

        return thread;
    }

    public void end() {
        UserSpace userSpace = this.coreManager.getUserSpace();

        if(ObjectUtil.isNull(userSpace)){
            throw new StatusNotReadyException();
        }

        Deque<ThreadObject> threads = userSpace.getThreads();

        if (ObjectUtil.isNull(threads) || threads.isEmpty()) {
            throw new StatusAlreadyFinishedException();
        }

        ThreadObject thread = threads.peek();

        ThreadStatusObject threadStatus = thread.getStatus();
        threadStatus.die();

        threads.pop();
    }
}
