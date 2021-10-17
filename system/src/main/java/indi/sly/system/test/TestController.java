package indi.sly.system.test;

import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.services.faces.AController;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessSessionObject;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.values.AccountAuthorizationSummaryDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        UUID processID = null;

        String processIDText = request.getParameter("ProcessID");
        if (!ValueUtil.isAnyNullOrEmpty(processIDText)) {
            processID = ObjectUtil.transferFromStringOrDefaultProvider(UUID.class, processIDText, () -> {
                throw new StatusUnreadableException();
            });
        }

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        threadManager.create(processID);

        Map<String, Object> result = new HashMap<>();

        //--Start--

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessSessionObject processSession = process.getSession();

        if (ValueUtil.isAnyNullOrEmpty(processSession.getID())) {
            processSession.create("Main");
        }
        result.put("ProcessSessionID", processSession.getID());

        UserManager userManager = this.factoryManager.getManager(UserManager.class);
        AccountAuthorizationObject authorize = userManager.authorize(process.getToken().getAccountID());
        AccountAuthorizationSummaryDefinition accountAuthorizationSummary = authorize.checkAndGetSummary();

        result.put("accountAuthorizationSummary", accountAuthorizationSummary);

        return result;
    }
}