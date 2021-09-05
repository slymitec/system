package indi.sly.system.boot.test;

import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
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
        Object ret = "finished";

        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoObject info = objectManager.get(List.of());
        InfoObject childInfo = info.getChild(new IdentificationDefinition("SLY"));

        childInfo.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);




        //childInfo.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);

        //AInfoContentObject infoContent = childInfo.getContent();

        //ret = infoContent.toString();

        childInfo.close();

        return ret;
    }
}
