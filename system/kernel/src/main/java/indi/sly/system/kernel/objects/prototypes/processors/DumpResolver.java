package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.lang.DumpFunction;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DumpResolver extends APrototype implements IInfoResolver {
    public DumpResolver() {
        this.dump = (dump, info, type, status) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            dump.getDate().put(DateTimeType.CREATE, nowDateTime);

            dump.getIdentifications().addAll(status.getIdentifications());
            dump.setOpen(status.getOpen());

            return dump;
        };
    }

    private final DumpFunction dump;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getDumps().add(this.dump);
    }
}
