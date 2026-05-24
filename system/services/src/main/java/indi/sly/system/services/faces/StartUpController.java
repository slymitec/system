package indi.sly.system.services.faces;

import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.CoreManager;
import indi.sly.system.kernel.core.boot.prototypes.BootObject;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.files.FileSystemManager;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.services.ServiceManager;
import indi.sly.system.services.core.environment.values.ServiceUserSpaceExtensionDefinition;
import indi.sly.system.services.jobs.JobService;
import indi.sly.system.services.jobs.values.ClientResponseDefinition;
import indi.sly.system.services.jobs.values.ClientResponseExceptionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@RestController
public class StartUpController extends AController {
    @RequestMapping(value = {"/StartUp.action"}, method = {RequestMethod.GET})
    @Transactional
    public ClientResponseDefinition startup(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init();

        if (ObjectUtil.isAnyNull(this.coreManager)) {
            this.coreManager = SpringHelper.getInstance(CoreManager.class);

            this.coreManager.startup(StartupType.STEP_INIT_SELF);
            this.coreManager.startup(StartupType.STEP_AFTER_SELF);

            KernelSpaceDefinition kernelSpace = this.coreManager.getKernelSpace();
            KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

            UserSpaceDefinition userSpace = new UserSpaceDefinition();
            userSpace.setServiceSpace(new ServiceUserSpaceExtensionDefinition());
            this.coreManager.setUserSpace(userSpace);
            this.coreManager.getObjectCollection().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

            BootObject boot = this.coreManager.getObjectCollection().getByClass(SpaceType.KERNEL, BootObject.class);
            FileSystemManager fileSystemManager = this.coreManager.getManager(FileSystemManager.class);
            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
            ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ServiceManager serviceManager = this.coreManager.getManager(ServiceManager.class);
            ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
            TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
            UserManager userManager = this.coreManager.getManager(UserManager.class);

            this.coreManager.getObjectCollection().addByClass(SpaceType.KERNEL, this.coreManager.create(JobService.class));

            JobService jobService = this.coreManager.getService(JobService.class);

            Long[] startups = new Long[]{
                    StartupType.STEP_INIT_SELF,
                    StartupType.STEP_AFTER_SELF,
                    StartupType.STEP_INIT_KERNEL,
                    StartupType.STEP_AFTER_KERNEL,
                    StartupType.STEP_INIT_SERVICE,
                    StartupType.STEP_AFTER_SERVICE
            };

            for (Long startup : startups) {
                memoryManager.startup(startup);
                boot.startup(startup);
                threadManager.startup(startup);
                processManager.startup(startup);
                typeManager.startup(startup);
                userManager.startup(startup);
                objectManager.startup(startup);
                fileSystemManager.startup(startup);
                serviceManager.startup(startup);

                jobService.startup(startup);
            }

            this.coreManager.setUserSpace(null);

            return new ClientResponseDefinition();
        } else {
            KernelSpaceDefinition kernelSpace = this.coreManager.getKernelSpace();
            KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

            ClientResponseDefinition clientResponse = new ClientResponseDefinition();

            ClientResponseExceptionDefinition clientResponseException = new ClientResponseExceptionDefinition();

            clientResponseException.setId(kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID);
            clientResponseException.setClazz(ClassUtil.getSimpleName(StatusAlreadyFinishedException.class));
            clientResponseException.setOwnerClazz(ClassUtil.getSimpleName(StartUpController.class));
            clientResponseException.setOwnerMethod("startup");

            clientResponse.setException(clientResponseException);

            return clientResponse;

        }
    }
}
