package indi.sly.subsystem.test;

import indi.sly.subsystem.periphery.core.CoreManager;
import indi.sly.subsystem.periphery.core.environment.containers.KernelSpace;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.core.CoreProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.core.DateTimeProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.SessionObject;
import indi.sly.subsystem.periphery.proxies.values.CallContextProcessRecord;
import indi.sly.subsystem.periphery.proxies.values.CallContextProcessType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.common.supports.UUIDUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Transactional
public class TestController {
    protected CoreManager coreManager;

    private void init() {
        KernelSpace kernelSpace = SpringHelper.getInstance(KernelSpace.class);

        this.coreManager = (CoreManager) kernelSpace.getClassedObjects().getOrDefault(CoreManager.class, null);

        if (ObjectUtil.allNotNull(this.coreManager)) {
            this.coreManager.check();
        }
    }

    @RequestMapping(value = {"/test"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object test() {
        if (ObjectUtil.isAnyNull(this.coreManager)) {
            this.init();
        }

        ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);

        UUID sessionId = UUIDUtil.getFormLongs(-1, -1);

        SessionObject session = proxyManager.getSession(sessionId);

        UUID PROCESSES_PROTOTYPE_SYSTEM_ID = UUIDUtil.getFormLongs(116714210840444914L, -8591569799439283374L);

        CallContextProcessRecord callContextProcess = new CallContextProcessRecord(PROCESSES_PROTOTYPE_SYSTEM_ID, CallContextProcessType.CLIENT, null, null);

        CoreProxyManager coreProxyManager = session.getServiceProxy(CoreProxyManager.class, callContextProcess);

        DateTimeProxyObject dateTime = coreProxyManager.getDateTime();

        long current = dateTime.getCurrent();

        return dateTime.getHandle().toString() + ": " + current;
    }

    @RequestMapping(value = {"/test2"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object test2() {
        if (ObjectUtil.isAnyNull(this.coreManager)) {
            this.init();
        }

        try {
            ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);

            UUID sessionId = UUIDUtil.getFormLongs(-1, -1);

            SessionObject session = proxyManager.getSession(sessionId);

            UUID PROCESSES_PROTOTYPE_SYSTEM_ID = UUIDUtil.getFormLongs(116714210840444914L, -8591569799439283374L);

            CallContextProcessRecord callContextProcess = new CallContextProcessRecord(PROCESSES_PROTOTYPE_SYSTEM_ID, CallContextProcessType.CLIENT, null, null);

            UUID handle = UUID.fromString("01a101df-b167-7f68-b0a8-a896e39e7711");

            DateTimeProxyObject dateTime = session.getProxy(DateTimeProxyObject.class, handle, callContextProcess);

            long current = dateTime.getCurrent();

            return dateTime.getHandle().toString() + ": " + current;
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }
}
