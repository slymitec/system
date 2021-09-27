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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
            StackTraceElement[] stackTrace = kernelException.getStackTrace();
            if (stackTrace.length != 0) {
                userContentException.setClazz(kernelException.getStackTrace()[0].getClassName());
                userContentException.setMethod(kernelException.getStackTrace()[0].getMethodName());
            }
            userContentException.setMessage(kernelException.getMessage());
        } else {
            Set<String> resultNames = jobContent.getResultNames();

            Map<String,String> results = new HashMap<>();

            for (String resultName : resultNames) {
                //??
            }
        }

        job.finish();

        this.fresh();
    }

    public UserContentResponseRawDefinition getResponse() {
        return null;
    }
}
