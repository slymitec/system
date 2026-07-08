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

import java.util.ArrayList;
import java.util.List;

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

        return this.definition.getContent().getRequest().task();
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

        return this.definition.getContent().getRequest().method();
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

        UserContentRequestRecord userContentRequest = this.definition.getContent().getRequest();

        JobService jobService = this.coreManager.getService(JobService.class);
        TaskObject task = jobService.getTask(userContentRequest.task());

        task.start();

        TaskContentObject taskContent = task.getContent();

        taskContent.setParameter(userContentRequest.parameters());

        task.run(userContentRequest.method());

        if (ObjectUtil.isAnyNull(taskContent.getException())) {
            Object result = taskContent.getResult();

            String clazz;
            if (ObjectUtil.allNotNull(result)) {
                clazz = ClassUtil.getSimpleName(result.getClass());
            } else {
                clazz = ClassUtil.getSimpleName(Void.class);
            }

            UserContentResponseRecord userContentResponse = new UserContentResponseRecord(userContentRequest.id(), ObjectUtil.transferToString(result), clazz);

            this.definition.getContent().setResponse(userContentResponse);
        } else {
            ASystemException systemException = taskContent.getException();

            List<ClientResponseExceptionTraceRecord> clientResponseExceptionTraces = new ArrayList<>();
            for (StackTraceElement stackTraceElement : systemException.getStackTrace()) {
                ClientResponseExceptionTraceRecord clientResponseExceptionTrace = new ClientResponseExceptionTraceRecord(ClassUtil.getSimpleName(stackTraceElement.getClass()), stackTraceElement.getMethodName());

                clientResponseExceptionTraces.add(clientResponseExceptionTrace);
            }

            ClientResponseExceptionRecord clientResponseException = new ClientResponseExceptionRecord(userContentRequest.id(), ClassUtil.getSimpleName(systemException.getClass()), clientResponseExceptionTraces);

            this.definition.setException(clientResponseException);
        }

        task.finish();
    }
}
