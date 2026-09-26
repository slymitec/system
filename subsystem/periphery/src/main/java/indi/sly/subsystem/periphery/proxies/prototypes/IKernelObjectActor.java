package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.proxies.values.CallRequestRecord;
import indi.sly.subsystem.periphery.proxies.values.ClientResponseRecord;
import reactor.core.publisher.Mono;

public interface IKernelObjectActor {
    Mono<ClientResponseRecord> call(CallRequestRecord callRequest);
}
