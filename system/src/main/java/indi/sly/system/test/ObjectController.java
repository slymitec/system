package indi.sly.system.test;

import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.values.InfoWildcardDefinition;
import indi.sly.system.services.face.AController;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.processes.ThreadManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
public class ObjectController extends AController {
    @RequestMapping(value = {"/ObjectsDisplay.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object objectsDisplay(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init();

        UserSpaceDefinition userSpace = new UserSpaceDefinition();
        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

        kernelSpace.getUserSpace().set(userSpace);
        this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        threadManager.create(kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID);

        //--Start--

        StringBuilder sb = new StringBuilder();

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        InfoObject info = objectManager.get(List.of());

        this.objectsDisplay(kernelConfiguration, sb, info);

        return sb.toString();
    }

    private void objectsDisplay(KernelConfigurationDefinition kernelConfiguration, StringBuilder paths, InfoObject info) {
        List<IdentificationDefinition> identifications = info.getIdentifications();

        StringBuilder path = new StringBuilder();
        for (IdentificationDefinition identification : identifications) {
            if (identification.getType().equals(UUID.class)) {
                path.append("/").append(UUIDUtil.readFormBytes(identification.getValue()));
            } else if (identification.getType().equals(String.class)) {
                path.append("/").append(StringUtil.readFormBytes(identification.getValue()));
            }
        }
        paths.append(path.isEmpty() ? "/" : path).append("\t(").append(info.getOpened()).append(")").append("<br />");

        UUID type = info.getType();
        if (type.equals(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID)
                || type.equals(kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID)) {
            Set<InfoSummaryDefinition> infoSummaries = info.queryChild(new InfoWildcardDefinition());
            for (InfoSummaryDefinition infoSummary : infoSummaries) {
                InfoObject childInfo = info.getChild(new IdentificationDefinition(infoSummary.getName()));
                this.objectsDisplay(kernelConfiguration, paths, childInfo);
            }
        } else if (type.equals(kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID)) {
            Set<InfoSummaryDefinition> infoSummaries = info.queryChild(new InfoWildcardDefinition());
            for (InfoSummaryDefinition infoSummary : infoSummaries) {
                InfoObject childInfo = info.getChild(new IdentificationDefinition(infoSummary.getID()));
                this.objectsDisplay(kernelConfiguration, paths, childInfo);
            }
        }
    }
}
