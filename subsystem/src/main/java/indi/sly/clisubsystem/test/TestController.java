package indi.sly.clisubsystem.test;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import io.dapr.actors.ActorId;
import io.dapr.actors.client.ActorClient;
import io.dapr.actors.client.ActorProxyBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Transactional
public class TestController {
    @RequestMapping(value = {"/UUID.action"}, method = {RequestMethod.GET})
    public Object uuid(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        StringBuilder result = new StringBuilder();

        UUID random;
        for (int i = 0; i < 128; i++) {
            random = UUIDUtil.createRandom();

            result.append(" UUIDUtil.getFormLongs(").append(random.getMostSignificantBits()).append("L, ").append(random.getLeastSignificantBits()).append("L);<br />");
        }

        return result.toString();
    }

    @RequestMapping(value = {"/Actor.action"}, method = {RequestMethod.GET})
    public Object actor(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try (ActorClient actorClient = new ActorClient()) {
            ActorProxyBuilder<ITestActor> builder = new ActorProxyBuilder<>(ITestActor.class, actorClient);

            ActorId actorId = new ActorId("ActorInstance1");

            ITestActor actor = builder.build(actorId);

            return actor.test("YZZSB2");
        }
    }
}
