package indi.sly.clisubsystem.test;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
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
    @RequestMapping(value = {"/Test.action"}, method = {RequestMethod.GET})
    public Object test(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        StringBuilder result = new StringBuilder();

        UUID random;
        for (int i = 0; i < 128; i++) {
            random = UUIDUtil.createRandom();

            result.append(" UUIDUtil.getFormLongs(").append(random.getMostSignificantBits()).append("L, ").append(random.getLeastSignificantBits()).append("L);<br />");
        }

        return result.toString();
    }
}
