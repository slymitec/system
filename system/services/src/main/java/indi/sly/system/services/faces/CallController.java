package indi.sly.system.services.faces;

import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.services.jobs.JobService;
import indi.sly.system.services.jobs.prototypes.UserContentObject;
import indi.sly.system.services.jobs.prototypes.UserContextObject;
import indi.sly.system.services.jobs.values.ClientResponseDefinition;
import indi.sly.system.services.jobs.values.ClientResponseExceptionDefinition;
import indi.sly.system.services.jobs.values.ClientRequestDefinition;
import indi.sly.system.services.jobs.values.ClientResponseExceptionTraceDefinition;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallController extends AController {
    protected void initCallController(HttpSession session) {
        UserSpaceDefinition userSpace = (UserSpaceDefinition) session.getAttribute("userSpace");

        if (ObjectUtil.isAnyNull(this.coreManager)) {
            synchronized (this) {
                if (ObjectUtil.isAnyNull(this.coreManager)) {
                    this.init();
                }
            }
        }

        if (ObjectUtil.isAnyNull(userSpace)) {
            throw new StatusNotReadyException();
        }

        this.coreManager.setUserSpace(userSpace);

        if (this.coreManager.getObjectCollection().getLimit(SpaceType.USER) <= 0L) {
            KernelSpaceDefinition kernelSpace = this.coreManager.getKernelSpace();
            KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();
            this.coreManager.getObjectCollection().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);
        }
    }

    @RequestMapping(value = {"/Call.action"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String onMessage(@RequestBody ClientRequestDefinition userContextRequest, HttpSession session) {
        this.initCallController(session);

        try {
            JobService jobService = this.coreManager.getService(JobService.class);

            UserContextObject userContext = jobService.createUserContext(userContextRequest);

            UserContentObject userContent = userContext.getContent();

            userContent.run();

            ClientResponseDefinition clientResponse = userContext.getResponse();

            jobService.finishUserContext(userContext);

            this.coreManager.setUserSpace(null);

            return ObjectUtil.transferToString(clientResponse);
        } catch (RuntimeException exception) {
            ClientResponseDefinition clientResponse = new ClientResponseDefinition();

            ClientResponseExceptionDefinition clientResponseException = new ClientResponseExceptionDefinition();

            clientResponseException.setId(UUIDUtil.getEmpty());

            clientResponseException.setClazz(ClassUtil.getSimpleName(exception.getClass()));
            for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
                ClientResponseExceptionTraceDefinition clientResponseExceptionTrace = new ClientResponseExceptionTraceDefinition();

                clientResponseExceptionTrace.setClazz(ClassUtil.getSimpleName(stackTraceElement.getClass()));
                clientResponseExceptionTrace.setMethod(stackTraceElement.getMethodName());

                clientResponseException.getTrace().add(clientResponseExceptionTrace);
            }

            clientResponse.setException(clientResponseException);

            return ObjectUtil.transferToString(clientResponse);
        }
    }
}
