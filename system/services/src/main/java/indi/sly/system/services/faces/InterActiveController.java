package indi.sly.system.services.faces;

import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.services.core.environment.values.ServiceUserSpaceExtensionDefinition;
import indi.sly.system.services.jobs.JobService;
import indi.sly.system.services.jobs.prototypes.UserContentObject;
import indi.sly.system.services.jobs.prototypes.UserContextObject;
import indi.sly.system.services.jobs.values.ClientResponseDefinition;
import indi.sly.system.services.jobs.values.ClientResponseExceptionDefinition;
import indi.sly.system.services.jobs.values.UserContentResponseDefinition;
import indi.sly.system.services.jobs.values.ClientRequestDefinition;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

@RestController
@ServerEndpoint(value = "/InterActive.action")
public class InterActiveController extends AController {
    public InterActiveController() {
    }

    private UserSpaceDefinition userSpace;

    @OnOpen
    public void onOpen(Session session) {
        this.init();

        if (ObjectUtil.isAnyNull(this.coreManager)) {
            try {
                session.close();
            } catch (IOException ignored) {
            }

            return;
        }

        this.userSpace = new UserSpaceDefinition();
        this.userSpace.setServiceSpace(new ServiceUserSpaceExtensionDefinition());

        this.coreManager.setUserSpace(this.userSpace);

        KernelSpaceDefinition kernelSpace = this.coreManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();
        this.coreManager.getObjectCollection().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        this.userSpace = null;

        this.coreManager.setUserSpace(null);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            this.coreManager.setUserSpace(this.userSpace);

            if (ValueUtil.isAnyNullOrEmpty(message)) {
                throw new StatusUnreadableException();
            }

            ClientRequestDefinition userContextRequest =
                    ObjectUtil.transferFromStringOrDefaultProvider(ClientRequestDefinition.class,
                            message, () -> {
                                throw new StatusUnreadableException();
                            });

            JobService jobService = this.coreManager.getService(JobService.class);

            UserContextObject userContext = jobService.createUserContext(userContextRequest);

            UserContentObject userContent = userContext.getContent();

            userContent.run();

            ClientResponseDefinition clientResponse = userContext.getResponse();

            jobService.finishUserContext(userContext);

            session.getAsyncRemote().sendText(ObjectUtil.transferToString(clientResponse));
        } catch (RuntimeException exception) {
            ClientResponseDefinition clientResponse = new ClientResponseDefinition();

            ClientResponseExceptionDefinition userContentResponseException = new ClientResponseExceptionDefinition();

            userContentResponseException.setClazz(exception.getClass().getName());
            StackTraceElement[] kernelExceptionStackTrace = exception.getStackTrace();
            if (kernelExceptionStackTrace.length != 0) {
                try {
                    userContentResponseException.setOwnerClazz(Class.forName(kernelExceptionStackTrace[0].getClassName()).getName());
                } catch (ClassNotFoundException e) {
                    userContentResponseException.setOwnerClazz(StatusUnexpectedException.class.getName());
                }
                userContentResponseException.setMethod(kernelExceptionStackTrace[0].getMethodName());
            }
            String[] kernelExceptionStackTraceMessage = new String[kernelExceptionStackTrace.length];
            for (int i = 0; i < kernelExceptionStackTrace.length; i++) {
                kernelExceptionStackTraceMessage[i] = kernelExceptionStackTrace[i].getClassName() + "." + kernelExceptionStackTrace[i].getMethodName() + "(...)";
            }
            userContentResponseException.setMessage(String.join(", ", kernelExceptionStackTraceMessage));

            clientResponse.setException(userContentResponseException);

            session.getAsyncRemote().sendText(ObjectUtil.transferToString(clientResponse));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
    }
}