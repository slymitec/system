package indi.sly.system.boot.test;

import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.files.instances.prototypes.FileSystemFolderContentObject;
import indi.sly.system.kernel.files.instances.values.FileSystemLocationType;
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
public class FileController extends ATController {
    @RequestMapping(value = {"/FRTest.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object fRTest(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

        Object ret = "finished";

        return ret;
    }

    @RequestMapping(value = {"/FMTest.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object fMTest(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

        Object ret = "finished";

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoObject filesInfo = objectManager.get(List.of(new IdentificationDefinition("Files")));

        InfoObject volumeInfo = this.md(filesInfo, "Volume", true);

        FileSystemFolderContentObject infoContent = (FileSystemFolderContentObject) volumeInfo.getContent();

        infoContent.setType(FileSystemLocationType.MAPPING);
        infoContent.setValue(StringUtil.writeToBytes("C:/Users/Sly/Desktop/SlySystem/Volume"));

        volumeInfo.close();

        this.mf(volumeInfo, "test.bin", false);

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
