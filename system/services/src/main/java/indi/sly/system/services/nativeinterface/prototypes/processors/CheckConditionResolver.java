package indi.sly.system.services.nativeinterface.prototypes.processors;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.nativeinterface.lang.FinishConsumer;
import indi.sly.system.services.nativeinterface.lang.RunConsumer;
import indi.sly.system.services.nativeinterface.lang.StartFunction;
import indi.sly.system.services.nativeinterface.prototypes.wrappers.NativeInterfaceProcessorMediator;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceStatusRuntimeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CheckConditionResolver extends APrototype implements INativeInterFaceResolver {
    public CheckConditionResolver() {
        this.start = (nativeInterface, status) -> {
            if (status.getRuntime() != NativeInterfaceStatusRuntimeType.INITIALIZATION) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.finish = (nativeInterface, status) -> {
            if (status.getRuntime() != NativeInterfaceStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.run = (nativeInterface, status, name, run, content) -> {
            if (status.getRuntime() != NativeInterfaceStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }
        };
    }

    @Override
    public int order() {
        return 0;
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
