package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.core.prototypes.AFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadFactory extends AFactory {
    @Override
    public void init() {
    }

    public ThreadObject buildThread(UUID processId) {
        ThreadObject thread = this.coreManager.create(ThreadObject.class);

        thread.setProcessId(processId);

        return thread;
    }

    public ThreadBuilder createThread() {
        ThreadBuilder threadBuilder = this.coreManager.create(ThreadBuilder.class);

        threadBuilder.factory = this;

        return threadBuilder;
    }
}
