package indi.sly.subsystem.applications.framework.controllers;

import cn.dev33.satoken.stp.StpUtil;
import indi.sly.subsystem.applications.framework.values.SystemCallRequestRecord;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.subsystem.periphery.proxies.prototypes.SessionObject;
import indi.sly.subsystem.periphery.proxies.values.ClientResponseRecord;
import indi.sly.subsystem.periphery.proxies.values.ClientResponseTypes;
import indi.sly.system.common.lang.ASystemException;
import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.ObjectUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CallController extends AController {
    @RequestMapping(value = {"/call.action"}, method = {RequestMethod.POST})
    public ClientResponseRecord call(@RequestBody SystemCallRequestRecord systemCallRequestRecord) {
        ClientResponseRecord clientResponse;

        try {
            if (ObjectUtil.isAnyNull(systemCallRequestRecord, systemCallRequestRecord.callContextProcess(), systemCallRequestRecord.callContextHandle(), systemCallRequestRecord.clientRequest())) {
                throw new ConditionParametersException();
            }
            if (StpUtil.isLogin()) {
                throw new StatusRelationshipErrorException();
            }

            UUID sessionId;
            try {
                sessionId = (UUID) StpUtil.getLoginId();
            } catch (Exception e) {
                throw new ConditionContextException();
            }

            ProxyManager proxyManager = this.coreManager.getManager(ProxyManager.class);

            SessionObject session = proxyManager.getSession(sessionId);

            String value = session.getProxyAndInvoke(systemCallRequestRecord.callContextHandle().taskName(), systemCallRequestRecord.callContextHandle().handle(), systemCallRequestRecord.callContextProcess(), systemCallRequestRecord.clientRequest());

            clientResponse = new ClientResponseRecord(ClientResponseTypes.NORMAL, value);
        } catch (ASystemException exception) {
            exception.printStackTrace(); //Temporary
            clientResponse = new ClientResponseRecord(ClientResponseTypes.SYSTEM_EXCEPTION, ObjectUtil.transferToString(exception.getMessage()));
        } catch (Exception exception) {
            exception.printStackTrace(); //Temporary
            clientResponse = new ClientResponseRecord(ClientResponseTypes.OTHER_EXCEPTION, ObjectUtil.transferToString(exception.getMessage()));
        }

        return clientResponse;
    }
}
