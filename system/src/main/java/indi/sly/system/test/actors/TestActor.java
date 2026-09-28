package indi.sly.system.test.actors;

import indi.sly.system.common.supports.ObjectUtil;
import io.dapr.actors.ActorId;
import io.dapr.actors.runtime.AbstractActor;
import io.dapr.actors.runtime.ActorRuntimeContext;

public class TestActor extends AbstractActor implements ITestActor {
    public TestActor(ActorRuntimeContext runtimeContext, ActorId id) {
        super(runtimeContext, id);
    }

    @Override
    public String test(String test) {
        String s = ObjectUtil.transferToString(10000L);

        Long l = ObjectUtil.transferFromString(Long.class, s);

        return "Echo, " + s + " " + l.toString();
    }
}