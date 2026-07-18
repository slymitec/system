package indi.sly.system.services.faces;

import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.CoreManager;
import indi.sly.system.kernel.core.boot.prototypes.BootObject;
import indi.sly.system.kernel.core.boot.prototypes.IStartupCapable;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.environment.containers.KernelConfiguration;
import indi.sly.system.kernel.core.environment.containers.KernelSpace;
import indi.sly.system.kernel.core.environment.values.SpaceType;
import indi.sly.system.kernel.core.environment.containers.UserSpace;
import indi.sly.system.kernel.files.FileSystemManager;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.services.ServiceManager;
import indi.sly.system.services.core.environment.values.ServiceUserExtensionSpace;
import indi.sly.system.services.jobs.JobService;
import indi.sly.system.services.jobs.values.ClientResponseRecord;
import indi.sly.system.services.jobs.values.ClientResponseExceptionRecord;
import indi.sly.system.services.jobs.values.ClientResponseExceptionTraceRecord;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StartUpController extends AController {
    @RequestMapping(value = {"/StartUp.action"}, method = {RequestMethod.GET})
    @Transactional
    public ClientResponseRecord startup(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init();

        if (ObjectUtil.isAnyNull(this.coreManager)) {
            this.coreManager = SpringHelper.getInstance(CoreManager.class);

            this.coreManager.startup(StartupType.STEP_INIT_SELF);
            this.coreManager.startup(StartupType.STEP_AFTER_SELF);

            KernelSpace kernelSpace = this.coreManager.getKernelSpace();
            KernelConfiguration kernelConfiguration = kernelSpace.getConfiguration();

            UserSpace userSpace = SpringHelper.getInstance(UserSpace.class);
            userSpace.setServiceSpace(new ServiceUserExtensionSpace());
            this.coreManager.setUserSpace(userSpace);
            this.coreManager.getObjectCollection().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

            List<IStartupCapable> startupCapableManagers = new ArrayList<>();
            startupCapableManagers.add(this.coreManager.getManager(MemoryManager.class));
            startupCapableManagers.add(this.coreManager.getObjectCollection().getByClass(SpaceType.KERNEL, BootObject.class));
            startupCapableManagers.add(this.coreManager.getManager(ThreadManager.class));
            startupCapableManagers.add(this.coreManager.getManager(ProcessManager.class));
            startupCapableManagers.add(this.coreManager.getManager(TypeManager.class));
            startupCapableManagers.add(this.coreManager.getManager(UserManager.class));
            startupCapableManagers.add(this.coreManager.getManager(ObjectManager.class));
            startupCapableManagers.add(this.coreManager.getManager(FileSystemManager.class));
            startupCapableManagers.add(this.coreManager.getManager(ServiceManager.class));

            this.coreManager.getObjectCollection().addByClass(SpaceType.KERNEL, this.coreManager.create(JobService.class));
            startupCapableManagers.add(this.coreManager.getService(JobService.class));

            Long[] startups = new Long[]{
                    StartupType.STEP_INIT_SELF,
                    StartupType.STEP_AFTER_SELF,
                    StartupType.STEP_INIT_KERNEL,
                    StartupType.STEP_AFTER_KERNEL,
                    StartupType.STEP_INIT_SERVICE,
                    StartupType.STEP_AFTER_SERVICE
            };

            for (Long startup : startups) {
                for (IStartupCapable startupCapableManager : startupCapableManagers) {
                    startupCapableManager.startup(startup);
                }
            }

            this.coreManager.setUserSpace(null);

            return new ClientResponseRecord(null, null);
        } else {
            KernelSpace kernelSpace = this.coreManager.getKernelSpace();
            KernelConfiguration kernelConfiguration = kernelSpace.getConfiguration();


            List<ClientResponseExceptionTraceRecord> clientResponseExceptionTraces = new ArrayList<>();
            ClientResponseExceptionTraceRecord clientResponseExceptionTrace = new ClientResponseExceptionTraceRecord(ClassUtil.getSimpleName(StartUpController.class), "startup");
            clientResponseExceptionTraces.add(clientResponseExceptionTrace);

            ClientResponseExceptionRecord clientResponseException = new ClientResponseExceptionRecord(UUIDUtil.getEmpty(), ClassUtil.getSimpleName(StatusAlreadyFinishedException.class), clientResponseExceptionTraces);
            ClientResponseRecord clientResponse = new ClientResponseRecord(clientResponseException);

            return clientResponse;
        }
    }
}
