package indi.sly.system.controllers.interactive;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.controllers.AController;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.services.job.JobService;
import indi.sly.system.services.job.prototypes.UserContentObject;
import indi.sly.system.services.job.prototypes.UserContextObject;
import indi.sly.system.services.job.values.UserContentResponseRawDefinition;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@RestController
@ServerEndpoint(value = "/Call")
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

        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

        kernelSpace.getUserSpace().set(this.userSpace);
        this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        this.userSpace = null;

        this.factoryManager.getKernelSpace().getUserSpace().set(null);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        this.factoryManager.getKernelSpace().getUserSpace().set(this.userSpace);

        JobService jobService = this.factoryManager.getService(JobService.class);

        UserContextObject userContext = jobService.createUserContext(message);

        UserContentObject userContextContent = userContext.getContent();

        userContextContent.run();

        UserContentResponseRawDefinition userContentResponseRaw = userContext.getResponse();

        session.getAsyncRemote().sendText(ObjectUtil.transferToString(userContentResponseRaw));
    }

    @OnError
    public void onError(Session session, Throwable error) {
    }
}