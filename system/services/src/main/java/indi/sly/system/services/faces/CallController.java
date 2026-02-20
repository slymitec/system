package indi.sly.system.services.faces;

import indi.sly.system.common.lang.StatusNotReadyException;
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
import indi.sly.system.services.jobs.values.UserContextRequestDefinition;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallController extends AController {
    protected void initCallController(HttpSession session) {
        UserSpaceDefinition userSpace = (UserSpaceDefinition) session.getAttribute("userSpace");

        if (ObjectUtil.isAnyNull(this.factoryManager)) {
            synchronized (this) {
                if (ObjectUtil.isAnyNull(this.factoryManager)) {
                    this.init();
                }
            }
        }

        if (ObjectUtil.isAnyNull(userSpace)) {
            throw new StatusNotReadyException();
        }

        this.factoryManager.setUserSpace(userSpace);

        if (this.factoryManager.getCoreObjectRepository().getLimit(SpaceType.USER) <= 0L) {
            KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
            KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();
            this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);
        }
    }

    @RequestMapping(value = {"/Call.action"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String onMessage(@RequestBody UserContextRequestDefinition userContextRequest, HttpSession session) {
        this.initCallController(session);

        try {
            JobService jobService = this.factoryManager.getService(JobService.class);

            UserContextObject userContext = jobService.createUserContext(userContextRequest);

            UserContentObject userContent = userContext.getContent();

            userContent.run();

            UserContentResponseDefinition userContentResponse = userContext.getResponse();

            jobService.finishUserContext(userContext);

            this.factoryManager.setUserSpace(null);

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
