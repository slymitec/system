package indi.sly.system.boot.test;

import indi.sly.system.common.ABase;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.FactoryManager;
import indi.sly.system.kernel.core.boot.prototypes.BootObject;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.SessionManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.security.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

@RestController
@Transactional
public class BootController extends ABase {
    @Autowired
    private FactoryManager factoryManager;

    private String ret;

    public String getRet() {
        return this.ret;
    }

    @RequestMapping(value = {"/Boot.action", "/Boot.do"}, method = {RequestMethod.GET})
    @Transactional
    public String Boot(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        if (!ValueUtil.isAnyNullOrEmpty(this.ret)) {
            return "----Boot-Already-Finished----";
        }

        this.factoryManager.startup(StartupType.STEP_INIT_SELF);
        this.factoryManager.startup(StartupType.STEP_AFTER_SELF);

        BootObject boot = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, BootObject.class);
        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        Long[] startups = new Long[]{
                StartupType.STEP_INIT_SELF,
                StartupType.STEP_AFTER_SELF,
                StartupType.STEP_INIT_KERNEL,
                StartupType.STEP_AFTER_KERNEL
        };

        for (Long startup : startups) {
            memoryManager.startup(startup);
            boot.startup(startup);
            threadManager.startup(startup);
            processManager.startup(startup);
            typeManager.startup(startup);
            userManager.startup(startup);
            objectManager.startup(startup);
            sessionManager.startup(startup);

        }

        this.ret = "----Boot-Finished----";

        return this.ret;
    }
}
