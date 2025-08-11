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
import indi.sly.system.services.jobs.values.UserContentResponseExceptionDefinition;
import indi.sly.system.services.jobs.values.UserContentResponseDefinition;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

@RestController
@ServerEndpoint(value = "/Call.action")
public class InterActiveController extends AController {
    public InterActiveController() {
    }

    private UserSpaceDefinition userSpace;

    @OnOpen
    public void onOpen(Session session) {
        this.init();

        if (ObjectUtil.isAnyNull(this.factoryManager)) {
            try {
                session.close();
            } catch (IOException ignored) {
            }

            return;
        }

        this.userSpace = new UserSpaceDefinition();
        this.userSpace.setServiceSpace(new ServiceUserSpaceExtensionDefinition());

        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

        this.factoryManager.setUserSpace(this.userSpace);
        this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        this.userSpace = null;

        this.factoryManager.setUserSpace(null);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            this.factoryManager.setUserSpace(this.userSpace);

            JobService jobService = this.factoryManager.getService(JobService.class);

            UserContextObject userContext = jobService.createUserContext(message);

            UserContentObject userContent = userContext.getContent();

            userContent.run();

            UserContentResponseDefinition userContentResponse = userContext.getResponse();

            jobService.finishUserContext(userContext);

            session.getAsyncRemote().sendText(ObjectUtil.transferToString(userContentResponse));
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

            session.getAsyncRemote().sendText(ObjectUtil.transferToString(userContentResponse));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
    }
}