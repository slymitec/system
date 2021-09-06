package indi.sly.system.boot.test;

import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@RestController
@Transactional
public class ObjectController extends AController {
    @RequestMapping(value = {"/ObjectTest.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object objectTest(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);
        Object ret = "finished";

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoObject info = objectManager.get(List.of());

        Set<InfoSummaryDefinition> infoSummaries = info.queryChild(infoSummary -> true);

        ret = infoSummaries;

        InfoObject childInfo;

//        for (int i = 0; i < 10; i++) {
//            childInfo = info.createChildAndOpen(this.kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID,
//                    new IdentificationDefinition(Integer.toString(i)), InfoOpenAttributeType.OPEN_EXCLUSIVE);
//            childInfo.close();
//        }

        return ret;
    }
}
