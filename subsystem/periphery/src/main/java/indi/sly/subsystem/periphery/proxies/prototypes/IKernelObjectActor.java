package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.proxies.values.CallRequestRecord;
import indi.sly.subsystem.periphery.proxies.values.ClientResponseRecord;
import io.dapr.actors.ActorMethod;
import io.dapr.actors.ActorType;
import reactor.core.publisher.Mono;

@ActorType(name = "KernelObjectActor")
public interface IKernelObjectActor {
    @ActorMethod(name = "call", returns = ClientResponseRecord.class)
    Mono<ClientResponseRecord> call(CallRequestRecord callRequest);

    @ActorMethod(name = "toString")
    String toString();
}
