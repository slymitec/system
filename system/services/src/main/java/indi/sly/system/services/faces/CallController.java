package indi.sly.system.services.faces;

import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.enviroment.values.KernelConfiguration;
import indi.sly.system.kernel.core.enviroment.values.KernelSpace;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpace;
import indi.sly.system.services.core.environment.values.ServiceUserSpaceExtensionDefinition;
import indi.sly.system.services.jobs.JobService;
import indi.sly.system.services.jobs.prototypes.UserContentObject;
import indi.sly.system.services.jobs.prototypes.UserContextObject;
import indi.sly.system.services.jobs.values.ClientResponseRecord;
import indi.sly.system.services.jobs.values.ClientResponseExceptionRecord;
import indi.sly.system.services.jobs.values.ClientRequestRecord;
import indi.sly.system.services.jobs.values.ClientResponseExceptionTraceRecord;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CallController extends AController {
    protected void initCallController() {
        if (ObjectUtil.isAnyNull(this.coreManager)) {
            synchronized (this) {
                if (ObjectUtil.isAnyNull(this.coreManager)) {
                    this.init();
                }
            }
        }

        UserSpace userSpace = SpringHelper.getInstance(UserSpace.class);
        userSpace.setServiceSpace(new ServiceUserSpaceExtensionDefinition());

        this.coreManager.setUserSpace(userSpace);

        if (this.coreManager.getObjectCollection().getLimit(SpaceType.USER) <= 0L) {
            KernelSpace kernelSpace = this.coreManager.getKernelSpace();
            KernelConfiguration kernelConfiguration = kernelSpace.getConfiguration();
            this.coreManager.getObjectCollection().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);
        }
    }

    @RequestMapping(value = {"/Call.action"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String onMessage(@RequestBody ClientRequestRecord userContextRequest) {
        this.initCallController();

        ClientResponseRecord clientResponse;
        try {
            JobService jobService = this.coreManager.getService(JobService.class);

            UserContextObject userContext = jobService.createUserContext(userContextRequest);
            UserContentObject userContent = userContext.getContent();

            userContent.run();

            clientResponse = userContext.getResponse();
            jobService.finishUserContext(userContext);
        } catch (RuntimeException exception) {
            List<ClientResponseExceptionTraceRecord> ClientResponseExceptionTraceRecords = new ArrayList<>();
            for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
                ClientResponseExceptionTraceRecord clientResponseExceptionTrace = new ClientResponseExceptionTraceRecord(ClassUtil.getSimpleName(stackTraceElement.getClass()), stackTraceElement.getMethodName());

                ClientResponseExceptionTraceRecords.add(clientResponseExceptionTrace);
            }

            ClientResponseExceptionRecord clientResponseException = new ClientResponseExceptionRecord(UUIDUtil.getEmpty(), ClassUtil.getSimpleName(exception.getClass()), ClientResponseExceptionTraceRecords);
            clientResponse = new ClientResponseRecord(clientResponseException);

            return ObjectUtil.transferToString(clientResponse);
        } finally {
            this.coreManager.setUserSpace(null);
        }

        return ObjectUtil.transferToString(clientResponse);
    }
}
