package indi.sly.system.boot.test;

import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.services.job.JobService;
import indi.sly.system.services.job.instances.prototypes.processors.ManagerJobInitializer;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.prototypes.JobObject;
import indi.sly.system.services.job.values.JobAttributeType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController extends ATController {
    @RequestMapping(value = {"/Test.action"}, method = {RequestMethod.GET})
    public Object test(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        TranObject instance = SpringHelper.getInstance(TranObject.class);
        instance.doo(() -> {
            this.init(request, response, session);

            return null;
        });


        Map<String, Object> result = new HashMap<>();
        Object ret = instance.getClass().getName();

        JobService jobService = this.factoryManager.getService(JobService.class);

        JobObject job = jobService.createJob("Managers", JobAttributeType.NULL, null,
                this.factoryManager.create(ManagerJobInitializer.class));

        instance.doo(() -> {
            job.start();

            return null;
        });

        new RuntimeException();

        JobContentObject content = job.getContent();
        //content.setParameter("ProcessID", this.kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID);

        job.run("createThread");

        instance.doo(() -> {
            job.finish();

            return null;
        });


        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();

        ret = process.getID();

        return ret;
    }
}
