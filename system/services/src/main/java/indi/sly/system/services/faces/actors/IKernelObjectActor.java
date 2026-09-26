package indi.sly.system.services.faces.actors;

import indi.sly.system.services.jobs.values.CallRequestRecord;
import indi.sly.system.services.jobs.values.ClientResponseRecord;
import reactor.core.publisher.Mono;

public interface IKernelObjectActor {
    Mono<ClientResponseRecord> call(CallRequestRecord callRequest);
}
