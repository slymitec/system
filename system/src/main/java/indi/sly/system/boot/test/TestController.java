package indi.sly.system.boot.test;

import indi.sly.system.services.job.JobService;
import indi.sly.system.services.job.instances.prototypes.processors.ManagerJobInitializer;
import indi.sly.system.services.job.prototypes.JobObject;
import indi.sly.system.services.job.values.JobAttributeType;
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
@Transactional
public class TestController extends AController {
    @RequestMapping(value = {"/Test.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object test(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

        Map<String, Object> result = new HashMap<>();
        Object ret = result;

        JobService jobService = this.factoryManager.getService(JobService.class);

        JobObject managers = jobService.createJob("Managers", JobAttributeType.NULL, null,
                this.factoryManager.create(ManagerJobInitializer.class));

        managers.start();

        managers.finish();

        return ret;
    }
}
