package indi.sly.system.services.actors.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.CoreManager;
import indi.sly.system.kernel.core.environment.containers.KernelSpace;
import indi.sly.system.kernel.core.environment.containers.UserSpace;
import indi.sly.system.services.jobs.JobService;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.prototypes.TaskObject;
import indi.sly.system.services.jobs.prototypes.CallContextObject;
import indi.sly.system.services.jobs.values.*;
import io.dapr.actors.ActorId;
import io.dapr.actors.runtime.AbstractActor;
import io.dapr.actors.runtime.ActorMethodContext;
import io.dapr.actors.runtime.ActorRuntimeContext;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class KernelObjectActor extends AbstractActor implements IKernelObjectActor {
    protected KernelObjectActor(ActorRuntimeContext runtimeContext, ActorId id) {
        super(runtimeContext, id);
    }

    protected CoreManager coreManager;
    protected TaskObject task;

    @Override
    public Mono<Void> onActivate() {
        super.onActivate();

        if (ObjectUtil.isAnyNull(this.coreManager)) {
            KernelSpace kernelSpace = SpringHelper.getInstance(KernelSpace.class);

            this.coreManager = (CoreManager) kernelSpace.getClassedObjects().getOrDefault(CoreManager.class, null);

            if (ObjectUtil.allNotNull(this.coreManager)) {
                this.coreManager.check();
            }
        }

        if (ObjectUtil.isAnyNull(this.coreManager.getUserSpace())) {
            UserSpace userSpace = SpringHelper.getInstance(UserSpace.class);

            this.coreManager.setUserSpace(userSpace);
        }

        if (ObjectUtil.isAnyNull(this.task)) {
            String actorId = this.getId().toString();
            String[] actorIds = actorId.split("[/\\\\]");

            if (actorIds.length != 2) {
                throw new ConditionParametersException();
            }

            String taskName = actorIds[0];
            UUID handle = null;
            if (!LogicalUtil.isAnyExist(task.getAttribute(), TaskAttributeType.OBJECT_IS_NOT_CACHEABLE)) {
                handle = ValueUtil.isAnyNullOrEmpty(actorIds[1]) ? null : UUIDUtil.getFromString(actorIds[1]);
            }

            JobService jobService = this.coreManager.getService(JobService.class);

            this.task = jobService.getTask(taskName, handle);
        }

        this.task.start();

        return Mono.empty();
    }

    @Override
    public Mono<Void> onDeactivate() {
        super.onDeactivate();

        if (ObjectUtil.allNotNull(this.task)) {
            this.task.end();
        }

        if (ObjectUtil.allNotNull(this.coreManager)) {
            this.coreManager = null;
        }

        return Mono.empty();
    }

    @Override
    public Mono<Void> onPreActorMethod(ActorMethodContext actorMethodContext) {
        if (ObjectUtil.isAnyNull(this.coreManager, this.task)) {
            return Mono.error(new StatusUnexpectedException());
        }

        if (ObjectUtil.isAnyNull(this.coreManager.getUserSpace())) {
            UserSpace userSpace = SpringHelper.getInstance(UserSpace.class);

            this.coreManager.setUserSpace(userSpace);
        }

        return super.onPreActorMethod(actorMethodContext);
    }

    public Mono<ClientResponseRecord> call(CallRequestRecord callRequest) {
        CallContextRecord callContext = callRequest.callContext();
        ClientRequestRecord clientRequest = callRequest.clientRequest();

        ClientResponseRecord clientResponse;
        try {
            JobService jobService = this.coreManager.getService(JobService.class);

            CallContextObject userContext = jobService.createCallContext(callContext);

            TaskContentObject taskContent = this.task.getContent();
            taskContent.setParameter(clientRequest.parameters());
            taskContent.run(clientRequest.method());

            if (ObjectUtil.isAnyNull(taskContent.getException())) {
                Object result = taskContent.getResult();

                clientResponse = new ClientResponseRecord(ObjectUtil.transferToString(result));
            } else {
                return Mono.error(taskContent.getException());
            }

            jobService.endCallContext(userContext);
        } catch (Exception exception) {
            return Mono.error(exception);
        }

        return Mono.just(clientResponse);
    }
}
