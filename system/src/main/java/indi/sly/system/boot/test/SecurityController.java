package indi.sly.system.boot.test;

import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

@RestController
@Transactional
public class SecurityController extends AController {
    @RequestMapping(value = {"/SecurityTest.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object createUser(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

        Object ret = "finished";

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountAuthorizationObject authorize = userManager.authorize("Sly", null);

        ret = authorize.checkAndGetResult();


        return ret;
    }
}
