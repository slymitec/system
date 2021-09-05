package indi.sly.system.boot.test;

import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;

@RestController
@Transactional
public class ObjectController extends AController {
    @RequestMapping(value = {"/ObjectTest.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object objectTest(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

//        InfoObject info = objectManager.get(List.of());
//        InfoObject childInfo = info.createChildAndOpen(kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID,
//                new IdentificationDefinition("SLY"), InfoOpenAttributeType.OPEN_EXCLUSIVE);
//        childInfo.close();

        InfoObject info = objectManager.get(List.of());

        info.deleteChild(new IdentificationDefinition("SLY"));

        return "finished";
    }
}
