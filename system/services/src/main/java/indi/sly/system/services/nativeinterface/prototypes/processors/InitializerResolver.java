package indi.sly.system.services.nativeinterface.prototypes.processors;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.nativeinterface.lang.FinishConsumer;
import indi.sly.system.services.nativeinterface.lang.RunConsumer;
import indi.sly.system.services.nativeinterface.lang.StartFunction;
import indi.sly.system.services.nativeinterface.prototypes.wrappers.ANativeInterfaceInitializer;
import indi.sly.system.services.nativeinterface.prototypes.wrappers.NativeInterfaceProcessorMediator;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceStatusRuntimeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InitializerResolver extends APrototype implements INativeInterFaceResolver {
    public InitializerResolver() {
        this.start = (nativeInterface, status) -> {
            ANativeInterfaceInitializer initializer = nativeInterface.getInitializer();

            initializer.start(nativeInterface);
        };

        this.finish = (nativeInterface, status) -> {
            ANativeInterfaceInitializer initializer = nativeInterface.getInitializer();

            initializer.finish(nativeInterface);
        };

        this.run = (nativeInterface, status, name, run, content) -> {
            ANativeInterfaceInitializer initializer = nativeInterface.getInitializer();

            initializer.run(name, run, content);
        };
    }

    @Override
    public int order() {
        return 2;
    }

    private final StartFunction start;
    private final FinishConsumer finish;
    private final RunConsumer run;

    @Override
    public void resolve(NativeInterfaceProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
        processorMediator.getRuns().add(this.run);
    }
}
