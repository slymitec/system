package indi.sly.subsystem.test;

import indi.sly.subsystem.periphery.core.CoreManager;
import indi.sly.subsystem.periphery.core.environment.containers.KernelSpace;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.core.CoreProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.core.prototypes.DateTimeProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.objects.ObjectProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.InfoProxyObject;
import indi.sly.subsystem.periphery.proxies.prototypes.SessionObject;
import indi.sly.subsystem.periphery.proxies.values.CallContextProcessRecord;
import indi.sly.subsystem.periphery.proxies.values.CallContextProcessType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.DateTimeType;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.PathRecord;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
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

        CoreProxyManager coreManager = session.getManagerProxy(CoreProxyManager.class, callContextProcess);

        DateTimeProxyObject dateTime = coreManager.getDateTime();

        long current = dateTime.getCurrent();

        return dateTime.getHandle().toString() + ": " + current;
    }

    @RequestMapping(value = {"/test1"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object test1() {
        if (ObjectUtil.isAnyNull(this.coreManager)) {
            this.init();
        }

        ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);

        UUID sessionId = UUIDUtil.getFormLongs(-1, -1);

        SessionObject session = proxyManager.getSession(sessionId);

        UUID PROCESSES_PROTOTYPE_SYSTEM_ID = UUIDUtil.getFormLongs(116714210840444914L, -8591569799439283374L);

        CallContextProcessRecord callContextProcess = new CallContextProcessRecord(PROCESSES_PROTOTYPE_SYSTEM_ID, CallContextProcessType.CLIENT, null, null);

        ObjectProxyManager objectManager = session.getManagerProxy(ObjectProxyManager.class, callContextProcess);

        InfoProxyObject info = objectManager.get(new PathRecord(List.of(new IdentifierRecord("Files"))));

        Map<Long, Long> infoDate = info.getDate();

        return info.getHandle().toString() + ": " + infoDate.getOrDefault(DateTimeType.CREATE, null);
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

            UUID handle = UUID.fromString("01a1026c-c1b0-7214-ae1c-690bee6f82f2");

            DateTimeProxyObject dateTime = session.getProxy(DateTimeProxyObject.class, handle, callContextProcess);

            long current = dateTime.getCurrent();

            return dateTime.getHandle().toString() + ": " + current;

            //return "";
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }
}
