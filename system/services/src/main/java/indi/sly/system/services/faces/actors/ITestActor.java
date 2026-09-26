package indi.sly.system.services.faces.actors;

import io.dapr.actors.ActorMethod;
import io.dapr.actors.ActorType;

@ActorType(name = "TestActor")
public interface ITestActor {
    @ActorMethod(name = "test")
    String test(String text);
}
