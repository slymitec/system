package indi.sly.system.boot.test;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.files.instances.prototypes.FileSystemFileContentObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.values.ApplicationDefinition;
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
public class FileController extends AController {
    @RequestMapping(value = {"/FileTest.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object fileTest(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

        Object ret = "finished";

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoObject i1 = objectManager.get(List.of(new IdentificationDefinition("Files"),
                new IdentificationDefinition("Volume")));

        InfoObject i2 = i1.getChild(new IdentificationDefinition("file.txt"));

        i2.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);

        FileSystemFileContentObject i2Content = (FileSystemFileContentObject) i2.getContent();


        byte[] source = i2Content.read(0, (int) i2Content.length());

        String value = StringUtil.readFormBytes(source);

        ApplicationDefinition application = ObjectUtil.transferFromString(ApplicationDefinition.class, value);


        ret = application;
//        ApplicationDefinition applicationDefinition = new ApplicationDefinition();
//        applicationDefinition.setID(UUID.randomUUID());
//        applicationDefinition.setName("TestApp");
//        applicationDefinition.setSupportedSession(SessionType.CLI);
//        applicationDefinition.setServerURL("http://1.2.3.4:1234");
//        applicationDefinition.getConfigurations().put("k1", "v1");
//        applicationDefinition.getConfigurations().put("k2", null);
//
//        String string = ObjectUtil.transferToString(applicationDefinition);

        i2.close();

        return ret;
    }

    private InfoObject mf(InfoObject info, String name) {
        return info.createChildAndOpen(this.kernelConfiguration.FILES_TYPES_INSTANCE_FILE_ID,
                new IdentificationDefinition(name), InfoOpenAttributeType.OPEN_EXCLUSIVE);
    }

    private void md(InfoObject info, String name) {
        InfoObject chidlInfo = info.createChildAndOpen(this.kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID,
                new IdentificationDefinition(name), InfoOpenAttributeType.OPEN_EXCLUSIVE);

        chidlInfo.close();
    }

    private void del(InfoObject info, String name) {
        info.deleteChild(new IdentificationDefinition(name));
    }
}
