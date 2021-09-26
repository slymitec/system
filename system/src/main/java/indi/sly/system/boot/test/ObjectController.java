package indi.sly.system.boot.test;

import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.instances.prototypes.SignalContentObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@RestController
@Transactional
public class ObjectController extends ATController {
    @RequestMapping(value = {"/ObjectTest.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object objectTest(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);
        Object ret = "finished";

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        UUID signalID = processManager.getCurrent().getCommunication().getSignalID();

        InfoObject info = objectManager.get(List.of(new IdentificationDefinition("Signals"),
                new IdentificationDefinition(signalID)));


        info.open(InfoOpenAttributeType.OPEN_SHARED_WRITE);

        SignalContentObject infoContent = (SignalContentObject) info.getContent();

        ret = infoContent.receive();


        info.close();

        return ret;
    }
}
