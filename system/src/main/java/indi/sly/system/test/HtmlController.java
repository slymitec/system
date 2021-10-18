package indi.sly.system.test;

import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.services.face.AController;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.processes.ThreadManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class HtmlController extends AController {
    @RequestMapping(value = {"/Html.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object Html(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init();

        if (ObjectUtil.allNotNull(this.factoryManager)) {
            UserSpaceDefinition userSpace = new UserSpaceDefinition();
            KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
            KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

            kernelSpace.getUserSpace().set(userSpace);
            this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

            UUID processID = null;

            String processIDText = request.getParameter("ProcessID");
            if (!ValueUtil.isAnyNullOrEmpty(processIDText)) {
                processID = ObjectUtil.transferFromStringOrDefaultProvider(UUID.class, processIDText, () -> {
                    throw new StatusUnreadableException();
                });
            }

            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
            if (!ValueUtil.isAnyNullOrEmpty(processID)) {
                threadManager.create(processID);
            }
        }

        Map<String, Object> result = new HashMap<>();

        //--Start--

        List<IdentificationDefinition> identifications =
                List.of(new IdentificationDefinition(UUIDUtil.createRandom()), new IdentificationDefinition("hello123"));

        result.put("ids", identifications);

        String value = ObjectUtil.transferToString(identifications);

        List<IdentificationDefinition> identifications2 = ObjectUtil.transferListFromString(IdentificationDefinition.class, value);
        if (identifications2 != null) {
            for (IdentificationDefinition identification : identifications2) {
                this.logger().error(identification.toString());
                this.logger().error(identification.getType().getName());
            }
        }

        //--End--

        return result;
    }
}