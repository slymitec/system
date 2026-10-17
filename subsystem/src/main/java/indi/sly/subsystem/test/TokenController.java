package indi.sly.subsystem.test;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import indi.sly.subsystem.periphery.core.CoreManager;
import indi.sly.subsystem.periphery.core.environment.containers.KernelSpace;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@Transactional
public class TokenController {
    protected CoreManager coreManager;

    private void init() {
        KernelSpace kernelSpace = SpringHelper.getInstance(KernelSpace.class);

        this.coreManager = (CoreManager) kernelSpace.getClassedObjects().getOrDefault(CoreManager.class, null);

        if (ObjectUtil.notNull(this.coreManager)) {
            this.coreManager.check();
        }
    }

    @RequestMapping(value = {"/ttest0"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object test() {
        if (ObjectUtil.isNull(this.coreManager)) {
            this.init();
        }

        UUID userId = new UUID(0, 7156);

        StpUtil.login(userId);

        return "ok";
    }

    @RequestMapping(value = {"/ttest1"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object test1() {
        if (ObjectUtil.isNull(this.coreManager)) {
            this.init();
        }

        boolean login = StpUtil.isLogin();

        Object loginId = StpUtil.getLoginId();

        SaSession session = StpUtil.getSession();

        session.set("ABC", Instant.now().toEpochMilli());

        return "" + login + " " + loginId;
    }

    @RequestMapping(value = {"/ttest2"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object test2() {
        if (ObjectUtil.isNull(this.coreManager)) {
            this.init();
        }

        boolean login = StpUtil.isLogin();

        Object loginId = StpUtil.getLoginId();

        SaSession session = StpUtil.getSession();

        long o = (long) session.get("ABC");

        return "" + login + " " + o;
    }
}
