package indi.sly.system.test.actors;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.CoreManager;
import indi.sly.system.kernel.core.environment.containers.KernelSpace;
import indi.sly.system.kernel.core.systemversion.prototypes.SystemVersionObject;
import io.dapr.actors.ActorId;
import io.dapr.actors.runtime.AbstractActor;
import io.dapr.actors.runtime.ActorRuntimeContext;

import java.util.UUID;

public class TestActor extends AbstractActor implements ITestActor {
    public TestActor(ActorRuntimeContext runtimeContext, ActorId id) {
        super(runtimeContext, id);
    }

    CoreManager coreManager;

    @Override
    public String test(String text) {
        return this.getId().toString() + ":" + text;
    }
}