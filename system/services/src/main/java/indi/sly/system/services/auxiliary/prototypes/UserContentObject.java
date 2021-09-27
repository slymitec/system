package indi.sly.system.services.auxiliary.prototypes;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.auxiliary.values.UserContentDefinition;
import indi.sly.system.services.auxiliary.values.UserContentExceptionDefinition;
import indi.sly.system.services.auxiliary.values.UserContextDefinition;
import indi.sly.system.services.job.JobService;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.prototypes.JobObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContentObject extends AIndependentValueProcessObject<UserContextDefinition> {
    public String getJob() {
        this.init();

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getID().equals(this.value.getThreadID())) {
            throw new StatusRelationshipErrorException();
        }

        return this.value.getContent().getJob();
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

        UserContentDefinition content = this.value.getContent();

        JobService jobService = this.factoryManager.getService(JobService.class);
        JobObject job = jobService.getJob(content.getJob());

        job.start();

        JobContentObject jobContent = job.getContent();

        Map<String, String> request = content.getRequest();
        for (Map.Entry<String, String> pair : request.entrySet()) {
            jobContent.setParameter(pair.getKey(), pair.getValue());
        }

        job.run(content.getMethod());

        if (jobContent.isException()) {
            AKernelException kernelException = jobContent.getException();
            UserContentExceptionDefinition userContentException = content.getException();

            userContentException.setName(kernelException.getClass().getSimpleName());
            StackTraceElement[] kernelExceptionStackTrace = kernelException.getStackTrace();
            if (kernelExceptionStackTrace.length != 0) {
                userContentException.setClazz(kernelExceptionStackTrace[0].getClassName());
                userContentException.setMethod(kernelExceptionStackTrace[0].getMethodName());
            }
            userContentException.setMessage(kernelException.getMessage());
        } else {
            content.getResponse().putAll(jobContent.getResult());
        }

        job.finish();

        this.fresh();
    }
}
