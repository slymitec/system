package indi.sly.system.services.faces;

import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.services.core.environment.values.ServiceUserSpaceExtensionDefinition;
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
public class InterActive2Controller extends AController {
    public InterActive2Controller() {
        this.init();
    }

    @RequestMapping(value = {"/Call2.action"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String onMessage(String message, HttpSession session) {
        UserSpaceDefinition userSpace = (UserSpaceDefinition) session.getAttribute("userSpace");
        session.setMaxInactiveInterval(60 * 60);

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
