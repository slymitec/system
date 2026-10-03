package indi.sly.system.services.actors.prototypes;

import indi.sly.system.services.jobs.values.CallRequestRecord;
import indi.sly.system.services.jobs.values.ClientResponseRecord;
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
