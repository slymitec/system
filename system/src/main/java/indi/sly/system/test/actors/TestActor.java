package indi.sly.system.test.actors;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.CoreManager;
import io.dapr.actors.ActorId;
import io.dapr.actors.runtime.AbstractActor;
import io.dapr.actors.runtime.ActorRuntimeContext;

public class TestActor extends AbstractActor implements ITestActor {
    public TestActor(ActorRuntimeContext runtimeContext, ActorId id) {
        super(runtimeContext, id);
    }

    CoreManager coreManager;

    @Override
    public String test(TestDTO text) {
        return this.getId().toString() + ":" + (ObjectUtil.isNull(text) ? StringUtil.EMPTY : text.getText());
    }
}