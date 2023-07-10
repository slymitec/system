package indi.sly.system.services.faces;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.services.core.environment.values.ServiceUserSpaceExtensionDefinition;
import indi.sly.system.services.jobs.JobService;
import indi.sly.system.services.jobs.prototypes.UserContentObject;
import indi.sly.system.services.jobs.prototypes.UserContextObject;
import indi.sly.system.services.jobs.values.UserContentExceptionDefinition;
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

        kernelSpace.getUserSpace().set(this.userSpace);
        this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        this.userSpace = null;

        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();

        kernelSpace.getUserSpace().set(this.userSpace);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            this.factoryManager.getKernelSpace().getUserSpace().set(this.userSpace);

            JobService jobService = this.factoryManager.getService(JobService.class);

            UserContextObject userContext = jobService.createUserContext(message);

            UserContentObject userContent = userContext.getContent();

            userContent.run();

            UserContentResponseDefinition userContentResponseRaw = userContext.getResponse();

            jobService.finishUserContext(userContext);

            session.getAsyncRemote().sendText(ObjectUtil.transferToString(userContentResponseRaw));
        } catch (RuntimeException exception) {
            UserContentResponseDefinition userContentResponseRaw = new UserContentResponseDefinition();

            UserContentExceptionDefinition userContentResponseExceptionRaw = userContentResponseRaw.getException();

            userContentResponseExceptionRaw.setName(exception.getClass().getSimpleName());
            StackTraceElement[] kernelExceptionStackTrace = exception.getStackTrace();
            if (kernelExceptionStackTrace.length != 0) {
                userContentResponseExceptionRaw.setClazz(kernelExceptionStackTrace[0].getClassName());
                userContentResponseExceptionRaw.setMethod(kernelExceptionStackTrace[0].getMethodName());
            }

            session.getAsyncRemote().sendText(ObjectUtil.transferToString(userContentResponseRaw));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
    }
}