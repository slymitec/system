package indi.sly.system.services.actors.prototypes;

import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.kernel.core.CoreManager;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.services.actors.tests.TestActor;
import io.dapr.actors.runtime.ActorFactory;
import io.dapr.actors.runtime.ActorRuntime;
import jakarta.inject.Named;

@Named
public class KernelObjectActorFactory extends AFactory {
    public <T extends KernelObjectActor> void register(Class<T> actorClass) {
        ActorFactory<T> actorFactory = (actorRuntimeContext, actorId) -> {
            T actor = SpringHelper.getInstance(actorClass, actorRuntimeContext, actorId);

            actor.coreManager = SpringHelper.getInstance(CoreManager.class);

            return actor;
        };

        ActorRuntime.getInstance().registerActor(actorClass, actorFactory);
        ActorRuntime.getInstance().registerActor(TestActor.class);
    }

    @Override
    public void init() {
        this.register(KernelObjectActor.class);
    }
}
