package indi.sly.system.services.nativeinterface.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.nativeinterface.lang.FinishConsumer;
import indi.sly.system.services.nativeinterface.lang.RunConsumer;
import indi.sly.system.services.nativeinterface.lang.StartFunction;
import indi.sly.system.services.nativeinterface.prototypes.wrappers.NativeInterfaceProcessorMediator;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceDefinition;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NativeInterfaceObject extends APrototype {
    protected NativeInterfaceFactory factory;
    protected NativeInterfaceProcessorMediator processorMediator;

    protected UUID id;
    protected NativeInterfaceStatusDefinition status;

    public void start() {
        NativeInterfaceDefinition nativeInterface = this.getSelf();

        List<StartFunction> resolvers = this.processorMediator.getStarts();

        for (StartFunction resolver : resolvers) {
            resolver.accept(nativeInterface, this.status);
        }
    }

    public void finish() {
        NativeInterfaceDefinition nativeInterface = this.getSelf();

        List<FinishConsumer> resolvers = this.processorMediator.getFinishes();

        for (FinishConsumer resolver : resolvers) {
            resolver.accept(nativeInterface, this.status);
        }
    }


    private synchronized NativeInterfaceDefinition getSelf() {
        return null;
    }

    public UUID getID() {
        return this.id;
    }

    public long getRuntime() {
        return this.status.getRuntime();
    }

    public synchronized void run(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        NativeInterfaceDefinition nativeInterface = this.getSelf();
        NativeInterfaceContentObject content = this.getContent();

        List<RunConsumer> resolvers = this.processorMediator.getRuns();

        for (RunConsumer resolver : resolvers) {
            resolver.accept(nativeInterface, this.status, name, this::run, content);
        }
    }

    public synchronized NativeInterfaceContentObject getContent() {
        return null;
    }
}
