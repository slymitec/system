package indi.sly.system.boot.test;

import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.files.instances.prototypes.FileSystemFileContentObject;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@Transactional
public class FileController extends AController {
    @RequestMapping(value = {"/FRTest.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object fRTest(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

        Object ret = "finished";

        InfoObject i1 = this.get("Files", "Main", "Test.txt");

        i1.close();

        i1 = this.get("Files", "Main");

        this.del(i1, "Test.txt");

        return ret;
    }

    @RequestMapping(value = {"/FMTest.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object fMTest(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

        Object ret = "finished";

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoObject i1 = objectManager.get(List.of(new IdentificationDefinition("Files"),
                new IdentificationDefinition("Volume")));

        InfoObject xiaoxiao = i1.createChildAndOpen(this.kernelConfiguration.FILES_TYPES_INSTANCE_FILE_ID,
                new IdentificationDefinition("Xiaoxiao.txt"), InfoOpenAttributeType.OPEN_EXCLUSIVE);

        FileSystemFileContentObject fc = (FileSystemFileContentObject) xiaoxiao.getContent();

        fc.write(StringUtil.writeToBytes("hello,羊羊爱笑笑。"));

        xiaoxiao.close();


//        InfoObject i2 = i1.getChild(new IdentificationDefinition("file.txt"));
//
//        i2.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);
//
//        FileSystemFileContentObject i2Content = (FileSystemFileContentObject) i2.getContent();
//
//
//        byte[] source = i2Content.read(0, (int) i2Content.length());
//
//        String value = StringUtil.readFormBytes(source);
//
//        ApplicationDefinition application = ObjectUtil.transferFromString(ApplicationDefinition.class, value);

//        ret = application;
//        ApplicationDefinition applicationDefinition = new ApplicationDefinition();
//        applicationDefinition.setID(UUID.randomUUID());
//        applicationDefinition.setName("TestApp");
//        applicationDefinition.setSupportedSession(SessionType.CLI);
//        applicationDefinition.setServerURL("http://1.2.3.4:1234");
//        applicationDefinition.getConfigurations().put("k1", "v1");
//        applicationDefinition.getConfigurations().put("k2", null);
//
//        String string = ObjectUtil.transferToString(applicationDefinition);

//        i2.close();

        return ret;
    }

    private InfoObject get(String... path) {
        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        List<IdentificationDefinition> identifications = new ArrayList<>();

        if (path != null && path.length > 0) {
            for (String pair : path) {
                identifications.add(new IdentificationDefinition(pair));
            }
        }

        return objectManager.get(identifications);
    }

    private InfoObject mf(InfoObject info, String name, boolean isOpen) {
        InfoObject chidlInfo = info.createChildAndOpen(this.kernelConfiguration.FILES_TYPES_INSTANCE_FILE_ID,
                new IdentificationDefinition(name), InfoOpenAttributeType.OPEN_EXCLUSIVE);

        if (!isOpen) {
            chidlInfo.close();
        }

        return chidlInfo;
    }

    private InfoObject md(InfoObject info, String name, boolean isOpen) {
        InfoObject chidlInfo = info.createChildAndOpen(this.kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID,
                new IdentificationDefinition(name), InfoOpenAttributeType.OPEN_EXCLUSIVE);

        if (!isOpen) {
            chidlInfo.close();
        }

        return chidlInfo;
    }

    private InfoObject md(InfoObject info, String name) {
        return this.md(info, name, false);
    }

    private void del(InfoObject info, String name) {
        info.deleteChild(new IdentificationDefinition(name));
    }
}
