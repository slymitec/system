package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.functions.*;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.definitions.DumpDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import indi.sly.system.kernel.objects.definitions.InfoStatusDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DumpProcessor extends ACoreObject implements IInfoObjectProcessor {
    public DumpProcessor() {
        this.dump = (dump, info, type, status) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            dump.getDate().put(DateTimeTypes.CREATE, nowDateTime);

            dump.getIdentifications().addAll(status.getIdentifications());
            dump.setOpen(status.getOpen());

            return dump;
        };
    }

    private final Function4<DumpDefinition, DumpDefinition, InfoEntity, TypeObject, InfoStatusDefinition> dump;

    @Override
    public void process(InfoEntity info, InfoObjectProcessorRegister processorRegister) {
        processorRegister.getDumps().add(this.dump);
    }

}
