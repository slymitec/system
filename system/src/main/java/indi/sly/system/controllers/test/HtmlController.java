package indi.sly.system.controllers.test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.controllers.AController;
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
import java.io.IOException;
import java.util.HashMap;
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

        Identification i1 = new Identification(UUID.randomUUID());
        Identification i2 = new Identification("Hello123");

        UUID u = UUID.randomUUID();
        String s = ObjectUtil.transferToString(u);

        result.put("1", i1);
        result.put("2", i2);
        result.put("u", u);
        result.put("s", s);

        //--End--

        return result;
    }

    public static class IdentificationSerializer extends JsonSerializer<Identification> {
        @Override
        public void serialize(Identification value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.toString());
        }
    }

    @JsonSerialize(using = IdentificationSerializer.class)
    public static class Identification extends ADefinition<Identification> {
        private byte[] id;
        private Class<?> type;

        public byte[] getID() {
            return this.id;
        }

        public Class<?> getType() {
            return this.type;
        }

        public Identification() {
            this.id = UUIDUtil.writeToBytes(UUIDUtil.getEmpty());
            this.type = UUID.class;
        }

        public Identification(UUID id) {
            if (ObjectUtil.isAnyNull(id)) {
                throw new ConditionParametersException();
            }

            this.id = UUIDUtil.writeToBytes(id);
            this.type = UUID.class;
        }

        public Identification(String id) {
            if (StringUtil.isNameIllegal(id)) {
                throw new ConditionParametersException();
            }

            this.id = StringUtil.writeToBytes(id);
            this.type = String.class;
        }

        @Override
        public String toString() {
            if (this.type == UUID.class) {
                return ObjectUtil.transferToString(UUIDUtil.readFormBytes(this.id));
            } else if (this.type == String.class) {
                return StringUtil.readFormBytes(this.id);
            } else {
                return null;
            }
        }
    }
}