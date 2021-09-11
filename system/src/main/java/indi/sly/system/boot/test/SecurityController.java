package indi.sly.system.boot.test;

import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.prototypes.GroupObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Set;

@RestController
@Transactional
public class SecurityController extends AController {
    @RequestMapping(value = {"/SecurityTest.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object createUser(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

        Object ret = "finished";

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        GroupObject group = userManager.getGroup("Administrators");

        AccountObject account = userManager.createAccount("Sly", "s34l510y24");

        account.setGroups(Set.of(group));

        return ret;
    }
}
