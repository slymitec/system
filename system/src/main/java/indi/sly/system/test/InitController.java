package indi.sly.system.test;

import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.files.instances.prototypes.FileSystemFileContentObject;
import indi.sly.system.kernel.files.instances.prototypes.FileSystemFolderContentObject;
import indi.sly.system.kernel.files.instances.values.FileSystemLocationType;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.values.InfoWildcardDefinition;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.services.face.AController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class InitController extends AController {
    @RequestMapping(value = {"/InitEnv.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object initEnv(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init();

        UserSpaceDefinition userSpace = new UserSpaceDefinition();
        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

        kernelSpace.getUserSpace().set(userSpace);
        this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        threadManager.create(kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID);

        Map<String, Object> result = new HashMap<>();

        //--Start--

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoObject parentInfo = objectManager.get(List.of(new IdentificationDefinition("Files")));

        InfoWildcardDefinition wildcard = new InfoWildcardDefinition("Volume");
        Set<InfoSummaryDefinition> infoSummaries = parentInfo.queryChild(wildcard);
        if (infoSummaries.isEmpty()) {
            InfoObject childInfo = parentInfo.createChildAndOpen(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID, new IdentificationDefinition("Volume"), InfoOpenAttributeType.OPEN_EXCLUSIVE);
            FileSystemFolderContentObject folderContent = (FileSystemFolderContentObject) childInfo.getContent();
            folderContent.setType(FileSystemLocationType.MAPPING);
            folderContent.setValue(StringUtil.writeToBytes("C:/Users/Sly/Desktop/SlySystem/Volume"));
            folderContent.close();
        }

        parentInfo = objectManager.get(List.of(new IdentificationDefinition("Files"), new IdentificationDefinition("Volume")));
        String fn = request.getParameter("fn");
        if (ValueUtil.isAnyNullOrEmpty(fn)) {
            wildcard = new InfoWildcardDefinition("Test.bin");
        } else {
            wildcard = new InfoWildcardDefinition(fn);
        }

        infoSummaries = parentInfo.queryChild(wildcard);
        if (infoSummaries.isEmpty()) {
//            InfoObject childInfo = parentInfo.createChildAndOpen(kernelConfiguration.FILES_TYPES_INSTANCE_FILE_ID, new IdentificationDefinition("Test.bin"), InfoOpenAttributeType.OPEN_EXCLUSIVE);
//            FileSystemFileContentObject fileContent = (FileSystemFileContentObject) childInfo.getContent();
//            fileContent.write(StringUtil.writeToBytes("{\"id\":\"f912d8f2-37ed-4c11-88e0-cb4a6e7eb147\",\"supportedSession\":2,\"name\":\"测试程序\",\"serverURL\":\"http://1.2.3.4\",\"configurations\":{\"配置1\":\"数值1\",\"配置2\":\"数值2\"}}"));
//            fileContent.close();
        } else {
            for (InfoSummaryDefinition infoSummary : infoSummaries) {
                InfoObject childInfo = parentInfo.getChild(new IdentificationDefinition(infoSummary.getName()));
                childInfo.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);
                FileSystemFileContentObject fileContent = (FileSystemFileContentObject) childInfo.getContent();
                fileContent.write(StringUtil.writeToBytes(Long.toString(System.currentTimeMillis())));
                fileContent.close();
            }


        }
        result.put("result", infoSummaries);

        return result;
    }
}
