package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.ASystemException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ADefinitionObject;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.jobs.JobService;
import indi.sly.system.services.jobs.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContentObject extends ADefinitionObject<UserContextDefinition> {
    public String getTask() {
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getId().equals(this.definition.getThreadId())) {
            throw new StatusRelationshipErrorException();
        }

        return this.definition.getContent().getRequest().getTask();
    }

    public String getMethod() {
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getId().equals(this.definition.getThreadId())) {
            throw new StatusRelationshipErrorException();
        }

        return this.definition.getContent().getRequest().getMethod();
    }

    public void run() {
        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        if (threadManager.size() == 0) {
            throw new StatusRelationshipErrorException();
        }
        ThreadObject thread = threadManager.getCurrent();
        if (!thread.getId().equals(this.definition.getThreadId())) {
            throw new StatusRelationshipErrorException();
        }

        UserContentRequestDefinition userContentRequest = this.definition.getContent().getRequest();
        UserContentResponseDefinition userContentResponse = this.definition.getContent().getResponse();

        userContentResponse.setId(userContentRequest.getId());

        JobService jobService = this.coreManager.getService(JobService.class);
        TaskObject task = jobService.getTask(userContentRequest.getTask());

        task.start();

        TaskContentObject taskContent = task.getContent();

        taskContent.setParameter(userContentRequest.getParameters());

        task.run(userContentRequest.getMethod());

        if (ObjectUtil.isAnyNull(taskContent.getException())) {
            Object result = taskContent.getResult();

            userContentResponse.setValue(ObjectUtil.transferToString(result));

            if (ObjectUtil.allNotNull(result)) {
                userContentResponse.setClazz(ClassUtil.getSimpleName(result.getClass()));
            } else {
                userContentResponse.setClazz(ClassUtil.getSimpleName(Void.class));
            }
        } else {
            ASystemException systemException = taskContent.getException();

            ClientResponseExceptionDefinition clientResponseException = this.definition.getException();

            clientResponseException.setId(userContentRequest.getId());
            clientResponseException.setClazz(ClassUtil.getSimpleName(systemException.getClass()));
            for (StackTraceElement stackTraceElement : systemException.getStackTrace()) {
                ClientResponseExceptionTraceDefinition clientResponseExceptionTrace = new ClientResponseExceptionTraceDefinition();

                clientResponseExceptionTrace.setClazz(ClassUtil.getSimpleName(stackTraceElement.getClass()));
                clientResponseExceptionTrace.setMethod(stackTraceElement.getMethodName());

                clientResponseException.getTrace().add(clientResponseExceptionTrace);
            }
        }

        task.finish();
    }
}
