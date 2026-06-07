package indi.sly.clisubsystem.test;

import indi.sly.system.common.supports.ObjectUtil;
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
        UUID uuid = null;

        String s = ObjectUtil.transferToString(uuid);

        UUID uuid1 = ObjectUtil.transferFromString(UUID.class, s);

        return uuid1 == null;
    }
}
