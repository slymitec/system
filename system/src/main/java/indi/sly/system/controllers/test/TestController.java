package indi.sly.system.controllers.test;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.controllers.AController;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.services.job.JobService;
import indi.sly.system.services.job.prototypes.UserContentObject;
import indi.sly.system.services.job.prototypes.UserContextObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.websocket.OnError;
import javax.websocket.Session;

@RestController
public class TestController extends AController {
    public TestController() {
    }

    private UserSpaceDefinition userSpace;


    @RequestMapping(value = {"/TestWS.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object Test(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init();

        if (ObjectUtil.isAnyNull(this.factoryManager)) {
            return "系统未启动。";
        }

//        UserContentRequestRawDefinition userContentRequestRaw = new UserContentRequestRawDefinition();
//
//        userContentRequestRaw.setTask("Manager");
//        userContentRequestRaw.setMethod("processGetCurrent");
//
//        if (2 > 1) {
//            return userContentRequestRaw;
//        }

        String requestText = """
                {"task":"Manager","method":"processGetCurrent","request":{}}
                """;

        this.userSpace = new UserSpaceDefinition();

        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

        kernelSpace.getUserSpace().set(this.userSpace);
        this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);


        JobService jobService = this.factoryManager.getService(JobService.class);

        UserContextObject userContext = jobService.createUserContext(requestText);

        UserContentObject userContextContent = userContext.getContent();

        userContextContent.run();

        return userContext.getResponse();
    }

    @OnError
    public void onError(Session session, Throwable error) {
    }
}