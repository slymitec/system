package indi.sly.system.test;

import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.services.faces.AController;
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
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.prototypes.GroupObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
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

        Set<InfoSummaryDefinition> infoSummaries = parentInfo.queryChild(infoSummary -> "Volume".equals(infoSummary.getName()));
        if (infoSummaries.isEmpty()) {
            InfoObject childInfo = parentInfo.createChildAndOpen(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID,
                    new IdentificationDefinition("Volume"), InfoOpenAttributeType.OPEN_EXCLUSIVE);
            FileSystemFolderContentObject folderContent = (FileSystemFolderContentObject) childInfo.getContent();
            folderContent.setType(FileSystemLocationType.MAPPING);
            folderContent.setValue(StringUtil.writeToBytes("C:/Users/Sly/Desktop/SlySystem/Volume"));
            folderContent.close();
        }

        parentInfo = objectManager.get(List.of(new IdentificationDefinition("Files"), new IdentificationDefinition("Volume")));
        infoSummaries = parentInfo.queryChild(infoSummary -> "Test.bin".equals(infoSummary.getName()));
        if (infoSummaries.isEmpty()) {
            InfoObject childInfo = parentInfo.createChildAndOpen(kernelConfiguration.FILES_TYPES_INSTANCE_FILE_ID,
                    new IdentificationDefinition("Test.bin"), InfoOpenAttributeType.OPEN_EXCLUSIVE);
            FileSystemFileContentObject fileContent = (FileSystemFileContentObject) childInfo.getContent();
            fileContent.write(StringUtil.writeToBytes("{\"id\":\"f912d8f2-37ed-4c11-88e0-cb4a6e7eb147\",\"supportedSession\":2,\"name\":\"测试程序\",\"serverURL\":\"http://1.2.3.4\",\"configurations\":{\"配置1\":\"数值1\",\"配置2\":\"数值2\"}}"));
            fileContent.close();
        }

        UserManager userManager = this.factoryManager.getManager(UserManager.class);
        Set<GroupObject> groups = new HashSet<>();
        groups.add(userManager.getGroup("Administrators"));
        groups.add(userManager.getGroup("Users"));
        try {
            AccountObject account = userManager.createAccount("Sly", "s34l510y24");
            account.setGroups(groups);
        } catch (Exception ignored) {
        }
        AccountAuthorizationObject accountAuthorization = userManager.authorize("Sly", null);

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        InfoObject execInfo = objectManager.get(List.of(new IdentificationDefinition("Files"),
                new IdentificationDefinition("Volume"), new IdentificationDefinition("Test.bin")));
        UUID handle = execInfo.open(InfoOpenAttributeType.OPEN_EXCLUSIVE);
        ProcessObject process = processManager.create(accountAuthorization, handle, null, null);

        result.put("ProcessID", process.getID());

        return result;
    }
}
