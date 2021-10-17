package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.job.JobService;
import indi.sly.system.services.job.values.UserContentDefinition;
import indi.sly.system.services.job.values.UserContentExceptionDefinition;
import indi.sly.system.services.job.values.UserContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContentObject extends AIndependentValueProcessObject<UserContextDefinition> {
    public String getTask() {
        this.init();

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getID().equals(this.value.getThreadID())) {
            throw new StatusRelationshipErrorException();
        }

        return this.value.getContent().getTask();
    }

    public String getMethod() {
        this.init();

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getID().equals(this.value.getThreadID())) {
            throw new StatusRelationshipErrorException();
        }

        return this.value.getContent().getMethod();
    }

    public void run() {
        this.init();

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getID().equals(this.value.getThreadID())) {
            throw new StatusRelationshipErrorException();
        }

        UserContentDefinition userContent = this.value.getContent();

        JobService jobService = this.factoryManager.getService(JobService.class);
        TaskObject task = jobService.getTask(userContent.getTask());

        task.start();

        TaskContentObject taskContent = task.getContent();

        Map<String, String> request = userContent.getRequest();
        for (Map.Entry<String, String> pair : request.entrySet()) {
            taskContent.setParameter(pair.getKey(), pair.getValue());
        }

        task.run(userContent.getMethod());

        if (ObjectUtil.allNotNull(taskContent.getException())) {
            AKernelException kernelException = taskContent.getException();
            UserContentExceptionDefinition userContentException = userContent.getException();

            userContentException.setName(kernelException.getClass().getSimpleName());
            StackTraceElement[] kernelExceptionStackTrace = kernelException.getStackTrace();
            if (kernelExceptionStackTrace.length != 0) {
                userContentException.setClazz(kernelExceptionStackTrace[0].getClassName());
                userContentException.setMethod(kernelExceptionStackTrace[0].getMethodName());
            }
            String[] kernelExceptionStackTraceMessage = new String[kernelExceptionStackTrace.length];
            for (int i = 0; i < kernelExceptionStackTrace.length; i++) {
                kernelExceptionStackTraceMessage[i] = kernelExceptionStackTrace[i].getClassName() + "." + kernelExceptionStackTrace[i].getMethodName() + "(...)";
            }
            userContentException.setMessage(String.join(", ", kernelExceptionStackTraceMessage));
        } else {
            userContent.getResponse().putAll(taskContent.getResult());
        }

        task.finish();

        this.fresh();
    }
}
