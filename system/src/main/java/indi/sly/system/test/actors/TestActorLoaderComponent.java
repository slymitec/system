package indi.sly.system.test.actors;

import indi.sly.system.kernel.core.prototypes.AComponent;
import io.dapr.actors.runtime.ActorRuntime;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TestActorLoaderComponent extends AComponent implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        ActorRuntime.getInstance().registerActor(TestActor.class);
    }
}
