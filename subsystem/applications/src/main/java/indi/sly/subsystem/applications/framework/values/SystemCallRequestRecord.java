package indi.sly.subsystem.applications.framework.values;

import indi.sly.subsystem.periphery.proxies.values.CallContextProcessRecord;
import indi.sly.subsystem.periphery.proxies.values.ClientRequestRecord;

public record SystemCallRequestRecord(CallContextProcessRecord callContextProcess,
                                      CallContextHandleRecord callContextHandle,
                                      ClientRequestRecord clientRequest) {
}
