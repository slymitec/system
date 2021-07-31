package indi.sly.system.services.nativeinterface.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.nativeinterface.lang.FinishConsumer;
import indi.sly.system.services.nativeinterface.lang.StartFunction;
import indi.sly.system.services.nativeinterface.prototypes.wrappers.NativeInterfaceProcessorMediator;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceStatusRuntimeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SetRuntimeResolver extends APrototype implements INativeInterFaceResolver {
    public SetRuntimeResolver() {
        this.start = (nativeInterface, status) -> {
            status.setRuntime(NativeInterfaceStatusRuntimeType.RUNNING);
        };

        this.finish = (nativeInterface, status) -> {
            status.setRuntime(NativeInterfaceStatusRuntimeType.FINISHED);
        };
    }

    @Override
    public int order() {
        return 3;
    }

    private final StartFunction start;
    private final FinishConsumer finish;

    @Override
    public void resolve(NativeInterfaceProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
    }
}
