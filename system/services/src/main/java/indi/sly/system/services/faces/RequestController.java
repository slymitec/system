package indi.sly.system.services.faces;

import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.services.jobs.JobService;
import indi.sly.system.services.jobs.prototypes.UserContentObject;
import indi.sly.system.services.jobs.prototypes.UserContextObject;
import indi.sly.system.services.jobs.values.UserContentResponseDefinition;
import indi.sly.system.services.jobs.values.UserContentResponseExceptionDefinition;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController extends AController {
    private void initUserSpace(UserSpaceDefinition userSpace) {
        synchronized (this) {
            if (ObjectUtil.isAnyNull(this.factoryManager)) {
                this.init();

                KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
                KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

                this.factoryManager.setUserSpace(userSpace);
                this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);
            }
        }
    }

    @RequestMapping(value = {"/Request.action"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String onMessage(String message, HttpSession session) {
        UserSpaceDefinition userSpace = (UserSpaceDefinition) session.getAttribute("userSpace");

        if (ObjectUtil.isAnyNull(this.factoryManager)) {
            this.initUserSpace(userSpace);
        }

        try {
            this.factoryManager.setUserSpace(userSpace);

            JobService jobService = this.factoryManager.getService(JobService.class);

            UserContextObject userContext = jobService.createUserContext(message);

            UserContentObject userContent = userContext.getContent();

            userContent.run();

            UserContentResponseDefinition userContentResponse = userContext.getResponse();

            jobService.finishUserContext(userContext);

            return ObjectUtil.transferToString(userContentResponse);
        } catch (RuntimeException exception) {
            UserContentResponseDefinition userContentResponse = new UserContentResponseDefinition();

            UserContentResponseExceptionDefinition userContentResponseException = userContentResponse.getException();

            userContentResponseException.setClazz(exception.getClass());
            StackTraceElement[] kernelExceptionStackTrace = exception.getStackTrace();
            if (kernelExceptionStackTrace.length != 0) {
                try {
                    userContentResponseException.setOwner(Class.forName(kernelExceptionStackTrace[0].getClassName()));
                } catch (ClassNotFoundException e) {
                    userContentResponseException.setOwner(StatusUnexpectedException.class);
                }
                userContentResponseException.setMethod(kernelExceptionStackTrace[0].getMethodName());
            }
            String[] kernelExceptionStackTraceMessage = new String[kernelExceptionStackTrace.length];
            for (int i = 0; i < kernelExceptionStackTrace.length; i++) {
                kernelExceptionStackTraceMessage[i] = kernelExceptionStackTrace[i].getClassName() + "." + kernelExceptionStackTrace[i].getMethodName() + "(...)";
            }
            userContentResponseException.setMessage(String.join(", ", kernelExceptionStackTraceMessage));

            return ObjectUtil.transferToString(userContentResponse);
        }
    }
}
