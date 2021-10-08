package indi.sly.system.controllers.test;

import indi.sly.system.controllers.AController;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController extends AController {
    @RequestMapping(value = {"/Test.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object Test(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init();

        UserSpaceDefinition userSpace = new UserSpaceDefinition();
        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

        kernelSpace.getUserSpace().set(userSpace);
        this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        threadManager.create(kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID);

        Map<String, Object> result = new HashMap<>();

        //--Start--

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);





        return result;
    }
}