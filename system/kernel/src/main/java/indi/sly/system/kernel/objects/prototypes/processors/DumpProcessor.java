package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.functions.*;
import indi.sly.system.kernel.core.date.DateTimeObject;
import indi.sly.system.kernel.core.date.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.prototypes.DumpDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import indi.sly.system.kernel.objects.prototypes.StatusDefinition;
import indi.sly.system.kernel.objects.prototypes.StatusOpenDefinition;
import indi.sly.system.kernel.objects.types.TypeObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DumpProcessor extends ACoreObject implements IInfoObjectProcessor {
    public DumpProcessor() {
        this.dump = (dump, info, type, status) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL, DateTimeObject.class);
            Date nowDateTime = dateTime.getCurrentDateTime();

            dump.getDate().put(DateTimeTypes.CREATE, nowDateTime);

            dump.getIdentifications().addAll(status.getIdentifications());
            dump.setOpen(status.getOpen());

            return dump;
        };
    }

    private final Function4<DumpDefinition, DumpDefinition, InfoEntity, TypeObject, StatusDefinition> dump;

    @Override
    public void postProcess(InfoEntity info, InfoObjectProcessorRegister processorRegister) {
        processorRegister.getDumps().add(this.dump);
    }

}
