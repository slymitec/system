package indi.sly.subsystem.test;

import indi.sly.subsystem.periphery.core.CoreManager;
import indi.sly.subsystem.periphery.core.environment.containers.KernelSpace;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.core.ICoreProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.core.IDateTimeProxy;
import indi.sly.subsystem.periphery.proxies.instances.core.prototypes.DateTimeProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.objects.ObjectProxyManager;
import indi.sly.subsystem.periphery.proxies.instances.objects.prototypes.InfoProxyObject;
import indi.sly.subsystem.periphery.proxies.instances.security.IAccountProxy;
import indi.sly.subsystem.periphery.proxies.instances.security.IGroupProxy;
import indi.sly.subsystem.periphery.proxies.instances.security.IUserProxyManager;
import indi.sly.subsystem.periphery.proxies.prototypes.SessionObject;
import indi.sly.subsystem.periphery.proxies.values.CallContextProcessRecord;
import indi.sly.subsystem.periphery.proxies.values.CallContextProcessType;
import indi.sly.subsystem.periphery.proxies.values.ClientRequestRecord;
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

import java.util.*;

@RestController
@Transactional
public class ProxyController {
    protected CoreManager coreManager;

    private void init() {
        KernelSpace kernelSpace = SpringHelper.getInstance(KernelSpace.class);

        this.coreManager = (CoreManager) kernelSpace.getClassedObjects().get(CoreManager.class);

        if (ObjectUtil.notNull(this.coreManager)) {
            this.coreManager.check();
        }
    }

    @RequestMapping(value = {"/ptest0"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object test0() {
        if (ObjectUtil.isNull(this.coreManager)) {
            this.init();
        }

        ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);

        UUID sessionId = UUIDUtil.getFormLongs(-1, -1);

        SessionObject session = proxyManager.getSession(sessionId);

        UUID PROCESSES_PROTOTYPE_SYSTEM_ID = UUIDUtil.getFormLongs(116714210840444914L, -8591569799439283374L);

        CallContextProcessRecord callContextProcess = new CallContextProcessRecord(PROCESSES_PROTOTYPE_SYSTEM_ID, CallContextProcessType.CLIENT, null, null);

        ICoreProxyManager coreManager = session.getManagerProxy(ICoreProxyManager.class, callContextProcess);

        IDateTimeProxy dateTime = coreManager.getDateTime();

        long current = dateTime.getCurrent();

        return dateTime.getHandle() + ": " + current;
    }

    @RequestMapping(value = {"/ptest1"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object test1() {
        if (ObjectUtil.isNull(this.coreManager)) {
            this.init();
        }

        ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);

        UUID sessionId = UUIDUtil.getFormLongs(-1, -1);

        SessionObject session = proxyManager.getSession(sessionId);

        UUID PROCESSES_PROTOTYPE_SYSTEM_ID = UUIDUtil.getFormLongs(116714210840444914L, -8591569799439283374L);

        CallContextProcessRecord callContextProcess = new CallContextProcessRecord(PROCESSES_PROTOTYPE_SYSTEM_ID, CallContextProcessType.CLIENT, null, null);

        ObjectProxyManager objectManager = session.getObjectManagerProxy(ObjectProxyManager.class, callContextProcess);

        InfoProxyObject info = objectManager.get(new PathRecord(List.of(new IdentifierRecord("Files"))));

        Map<Long, Long> infoDate = info.getDate();

        return info.getHandle().toString() + ": " + infoDate.getOrDefault(DateTimeType.CREATE, null);
    }

    @RequestMapping(value = {"/ptest1r"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object test1r() {
        if (ObjectUtil.isNull(this.coreManager)) {
            this.init();
        }

        try {
            ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);

            UUID sessionId = UUIDUtil.getFormLongs(-1, -1);

            SessionObject session = proxyManager.getSession(sessionId);

            UUID PROCESSES_PROTOTYPE_SYSTEM_ID = UUIDUtil.getFormLongs(116714210840444914L, -8591569799439283374L);

            CallContextProcessRecord callContextProcess = new CallContextProcessRecord(PROCESSES_PROTOTYPE_SYSTEM_ID, CallContextProcessType.CLIENT, null, null);

            UUID handle = UUID.fromString("01a10613-1c5f-7570-a16d-990ad2cc5b18");

            InfoProxyObject info = session.getObjectProxy(InfoProxyObject.class, handle, callContextProcess);

            System.out.println(info.getName());

            Map<Long, Long> infoDate = info.getDate();

            String r = "";

            for (Map.Entry<Long, Long> infoDatePair : infoDate.entrySet()) {
                String k = "";
                if (infoDatePair.getKey() == DateTimeType.CREATE) {
                    k = "CREATE";
                } else if (infoDatePair.getKey() == DateTimeType.ACCESS) {
                    k = "ACCESS";
                } else if (infoDatePair.getKey() == DateTimeType.MODIFIED) {
                    k = "MODIFIED";
                } else if (infoDatePair.getKey() == DateTimeType.EXPIRED) {
                    k = "EXPIRED";
                }
                r += k + ": " + infoDatePair.getValue().toString() + "; ";
            }

            return info.getHandle().toString() + ": " + r;
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }

    @RequestMapping(value = {"/ptest2"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object test2() {
        if (ObjectUtil.isNull(this.coreManager)) {
            this.init();
        }

        try {
            ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);

            UUID sessionId = UUIDUtil.getFormLongs(-1, -1);

            SessionObject session = proxyManager.getSession(sessionId);

            UUID PROCESSES_PROTOTYPE_SYSTEM_ID = UUIDUtil.getFormLongs(116714210840444914L, -8591569799439283374L);

            CallContextProcessRecord callContextProcess = new CallContextProcessRecord(PROCESSES_PROTOTYPE_SYSTEM_ID, CallContextProcessType.CLIENT, null, null);

            UUID handle = UUID.fromString("01a1026c-c1b0-7214-ae1c-690bee6f82f2");

            DateTimeProxyObject dateTime = session.getObjectProxy(DateTimeProxyObject.class, handle, callContextProcess);

            long current = dateTime.getCurrent();

            return dateTime.getHandle().toString() + ": " + current;

            //return "";
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }

    @RequestMapping(value = {"/ptest3"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object test3() {
        if (ObjectUtil.isNull(this.coreManager)) {
            this.init();
        }

        ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);

        UUID sessionId = UUIDUtil.getFormLongs(-1, -1);

        SessionObject session = proxyManager.getSession(sessionId);

        UUID PROCESSES_PROTOTYPE_SYSTEM_ID = UUIDUtil.getFormLongs(116714210840444914L, -8591569799439283374L);

        CallContextProcessRecord callContextProcess = new CallContextProcessRecord(PROCESSES_PROTOTYPE_SYSTEM_ID, CallContextProcessType.CLIENT, null, null);

        IUserProxyManager userManager = session.getManagerProxy(IUserProxyManager.class, callContextProcess);

        IAccountProxy currentAccount = userManager.getCurrentAccount();

        Set<IGroupProxy> groups = currentAccount.getGroups();

        String result = "";
        for (IGroupProxy group : groups) {
            result += group.getHandle() + ":" + group.getName() + "<br />";
        }

        return currentAccount.getHandle() + ": <br />" + result;
    }

    @RequestMapping(value = {"/ptest4"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object test4() {
        if (ObjectUtil.isNull(this.coreManager)) {
            this.init();
        }

        ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);

        UUID sessionId = UUIDUtil.getFormLongs(-1, -1);

        SessionObject session = proxyManager.getSession(sessionId);

        UUID PROCESSES_PROTOTYPE_SYSTEM_ID = UUIDUtil.getFormLongs(116714210840444914L, -8591569799439283374L);

        CallContextProcessRecord callContextProcess = new CallContextProcessRecord(PROCESSES_PROTOTYPE_SYSTEM_ID, CallContextProcessType.CLIENT, null, null);

        ClientRequestRecord clientRequest = new ClientRequestRecord("getCurrentAccount", new ArrayList<>());
        String s = session.getProxyAndInvoke("UserManager", new UUID(1, 2), callContextProcess, clientRequest);

        return s;
    }
}
