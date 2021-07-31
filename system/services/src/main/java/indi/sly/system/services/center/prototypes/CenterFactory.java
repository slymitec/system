package indi.sly.system.services.center.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.prototypes.processors.IProcessResolver;
import indi.sly.system.services.center.prototypes.processors.ICenterResolver;
import indi.sly.system.services.center.prototypes.wrappers.CenterProcessorMediator;
import indi.sly.system.services.center.values.CenterDefinition;
import indi.sly.system.services.center.values.CenterStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CenterFactory extends AFactory {
    protected List<ICenterResolver> centerResolvers;

    @Override
    public void init() {
        this.centerResolvers = new CopyOnWriteArrayList<>();

        Set<APrototype> corePrototypes =
                this.factoryManager.getCoreRepository().getByImplementInterface(SpaceType.KERNEL, IProcessResolver.class);

        for (APrototype prototype : corePrototypes) {
            if (prototype instanceof ICenterResolver) {
                this.centerResolvers.add((ICenterResolver) prototype);
            }
        }

        Collections.sort(this.centerResolvers);
    }

    private CenterObject build(CenterProcessorMediator processorMediator, Provider<CenterDefinition> funcRead,
                               Consumer1<CenterDefinition> funcWrite) {
        CenterObject center = this.factoryManager.create(CenterObject.class);

        center.setSource(funcRead, funcWrite);
        center.processorMediator = processorMediator;
        center.status = new CenterStatusDefinition();

        return center;
    }

    public CenterObject build(CenterDefinition center) {
        if (ObjectUtil.isAnyNull(center)) {
            throw new ConditionParametersException();
        }

        CenterProcessorMediator processorMediator =
                this.factoryManager.create(CenterProcessorMediator.class);
        for (ICenterResolver resolver : this.centerResolvers) {
            resolver.resolve(center, processorMediator);
        }

        return this.build(processorMediator, () -> center, (source) -> {
        });
    }
}
