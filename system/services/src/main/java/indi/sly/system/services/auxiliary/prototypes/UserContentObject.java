package indi.sly.system.services.auxiliary.prototypes;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.services.auxiliary.values.UserContentDefinition;
import indi.sly.system.services.auxiliary.values.UserContentExceptionDefinition;
import indi.sly.system.services.auxiliary.values.UserContentResponseRawDefinition;
import indi.sly.system.services.job.JobService;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.prototypes.JobObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContentObject extends AIndependentValueProcessObject<UserContentDefinition> {
    public String getJob() {
        this.init();

        return this.value.getJob();
    }

    public String getMethod() {
        this.init();

        return this.value.getMethod();
    }

    public void run() {
        this.init();

        JobService jobService = this.factoryManager.getService(JobService.class);

        JobObject job = jobService.getJob(this.value.getJob());

        job.start();

        JobContentObject jobContent = job.getContent();

        Map<String, String> request = this.value.getRequest();
        for (Map.Entry<String, String> pair : request.entrySet()) {
            jobContent.setParameter(pair.getKey(), pair.getValue());
        }

        job.run(this.value.getMethod());

        if (jobContent.isException()) {
            AKernelException kernelException = jobContent.getException();
            UserContentExceptionDefinition userContentException = this.value.getException();

            userContentException.setName(kernelException.getClass().getSimpleName());
            StackTraceElement[] kernelExceptionStackTrace = kernelException.getStackTrace();
            if (kernelExceptionStackTrace.length != 0) {
                userContentException.setClazz(kernelExceptionStackTrace[0].getClassName());
                userContentException.setMethod(kernelExceptionStackTrace[0].getMethodName());
            }
            userContentException.setMessage(kernelException.getMessage());
        } else {
            this.value.getResponse().putAll(jobContent.getResult());
        }

        job.finish();

        this.fresh();
    }

    public UserContentResponseRawDefinition getResponse() {
        this.init();

        UserContentResponseRawDefinition responseRaw = new UserContentResponseRawDefinition();

        responseRaw.getResponse().putAll(this.value.getResponse());
        UserContentExceptionDefinition exception = this.value.getException();
        responseRaw.getException().setName(exception.getName());
        responseRaw.getException().setClazz(exception.getClazz());
        responseRaw.getException().setMethod(exception.getMethod());
        responseRaw.getException().setMessage(exception.getMessage());

        return responseRaw;
    }
}
