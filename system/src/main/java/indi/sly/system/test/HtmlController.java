package indi.sly.system.test;

import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.kernel.core.environment.containers.KernelConfiguration;
import indi.sly.system.kernel.core.environment.containers.KernelSpace;
import indi.sly.system.kernel.core.environment.values.SpaceType;
import indi.sly.system.kernel.core.environment.containers.UserSpace;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.services.faces.AController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

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

        if (ObjectUtil.allNotNull(this.coreManager)) {
            UserSpace userSpace = new UserSpace();
            KernelSpace kernelSpace = this.coreManager.getKernelSpace();
            KernelConfiguration kernelConfiguration = kernelSpace.getConfiguration();

            kernelSpace.setUserSpace(userSpace);
            this.coreManager.getObjectCollection().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

            UUID processID = null;

            String processIDText = request.getParameter("ProcessID");
            if (!ValueUtil.isAnyNullOrEmpty(processIDText)) {
                processID = ObjectUtil.transferFromStringOrDefaultProvider(UUID.class, processIDText, () -> {
                    throw new StatusUnreadableException();
                });
            }

            ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
            if (!ValueUtil.isAnyNullOrEmpty(processID)) {
                threadManager.create(processID);
            }
        }

        Map<String, Object> result = new HashMap<>();

        //--Start--

        String j1 = ObjectUtil.transferToString("/234");
        String j2 = ObjectUtil.transferToString("234");
        String s1 = ObjectUtil.transferFromString(String.class, j1);
        String s2 = ObjectUtil.transferFromString(String.class, j2);

        this.logger().error(j1);
        this.logger().error(s1);
        this.logger().error(j2);
        this.logger().error(s2);


        List<IdentifierRecord> identifications =
                List.of(new IdentifierRecord(UUIDUtil.createRandom()), new IdentifierRecord("text"));

        result.put("ids", identifications);

        String value = ObjectUtil.transferToString(identifications);

        List<IdentifierRecord> identifications2 = ObjectUtil.transferListFromString(IdentifierRecord.class, value);
        if (identifications2 != null) {
            for (IdentifierRecord identification : identifications2) {
                this.logger().error(identification.toString());
                this.logger().error(ClassUtil.getSimpleName(identification.type()));
            }
        }

        //--End--

        return result;
    }
}