package indi.sly.system.services.faces;

import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.containers.KernelConfiguration;
import indi.sly.system.kernel.core.enviroment.containers.KernelSpace;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.containers.UserSpace;
import indi.sly.system.services.core.environment.values.ServiceUserExtensionSpace;
import indi.sly.system.services.jobs.JobService;
import indi.sly.system.services.jobs.prototypes.UserContentObject;
import indi.sly.system.services.jobs.prototypes.UserContextObject;
import indi.sly.system.services.jobs.values.ClientResponseRecord;
import indi.sly.system.services.jobs.values.ClientResponseExceptionRecord;
import indi.sly.system.services.jobs.values.ClientRequestRecord;
import indi.sly.system.services.jobs.values.ClientResponseExceptionTraceRecord;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@ServerEndpoint(value = "/InterActive.action")
public class InterActiveController extends AController {
    public InterActiveController() {
    }

    private UserSpace userSpace;

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

        this.userSpace = new UserSpace();
        this.userSpace.setServiceSpace(new ServiceUserExtensionSpace());

        this.coreManager.setUserSpace(this.userSpace);

        KernelSpace kernelSpace = this.coreManager.getKernelSpace();
        KernelConfiguration kernelConfiguration = kernelSpace.getConfiguration();
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

            ClientRequestRecord userContextRequest =
                    ObjectUtil.transferFromStringOrDefaultProvider(ClientRequestRecord.class,
                            message, () -> {
                                throw new StatusUnreadableException();
                            });

            JobService jobService = this.coreManager.getService(JobService.class);

            UserContextObject userContext = jobService.createUserContext(userContextRequest);

            UserContentObject userContent = userContext.getContent();

            userContent.run();

            ClientResponseRecord clientResponse = userContext.getResponse();

            jobService.finishUserContext(userContext);

            session.getAsyncRemote().sendText(ObjectUtil.transferToString(clientResponse));
        } catch (RuntimeException exception) {
            List<ClientResponseExceptionTraceRecord> clientResponseExceptionTraces = new ArrayList<>();
            for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
                ClientResponseExceptionTraceRecord clientResponseExceptionTrace = new ClientResponseExceptionTraceRecord(ClassUtil.getSimpleName(stackTraceElement.getClass()), stackTraceElement.getMethodName());

                clientResponseExceptionTraces.add(clientResponseExceptionTrace);
            }

            ClientResponseExceptionRecord clientResponseException = new ClientResponseExceptionRecord(UUIDUtil.getEmpty(), ClassUtil.getSimpleName(exception.getClass()), clientResponseExceptionTraces);
            ClientResponseRecord clientResponse = new ClientResponseRecord(clientResponseException);

            session.getAsyncRemote().sendText(ObjectUtil.transferToString(clientResponse));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
    }
}