package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.functions.Consumer4;
import indi.sly.system.common.functions.Function4;
import indi.sly.system.common.functions.Function6;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DateProcessor extends ACorePrototype implements IInfoObjectProcessor {
    public DateProcessor() {
        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceTypes.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = ObjectUtils.transferFromByteArray(info.getDate());
            date.put(DateTimeTypes.ACCESS, nowDateTime);
            info.setDate(ObjectUtils.transferToByteArray(date));

            return handle;
        };

        this.createChildAndOpen = (childInfo, info, type, status, childType, identification) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceTypes.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = new HashMap<>();
            date.put(DateTimeTypes.CREATE, nowDateTime);
            date.put(DateTimeTypes.MODIFIED, nowDateTime);
            date.put(DateTimeTypes.ACCESS, nowDateTime);
            childInfo.setDate(ObjectUtils.transferToByteArray(date));

            return childInfo;
        };

        this.readContent = (content, info, type, status) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceTypes.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = ObjectUtils.transferFromByteArray(info.getDate());
            date.put(DateTimeTypes.ACCESS, nowDateTime);
            info.setDate(ObjectUtils.transferToByteArray(date));

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceTypes.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = ObjectUtils.transferFromByteArray(info.getDate());
            date.put(DateTimeTypes.MODIFIED, nowDateTime);
            info.setDate(ObjectUtils.transferToByteArray(date));
        };
    }

    private final Function6<UUID, UUID, InfoEntity, TypeObject, InfoStatusDefinition, Long, Object[]> open;
    private final Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoStatusDefinition, UUID, Identification> createChildAndOpen;
    private final Function4<byte[], byte[], InfoEntity, TypeObject, InfoStatusDefinition> readContent;
    private final Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, byte[]> writeContent;

    @Override
    public void process(InfoEntity info, InfoObjectProcessorRegister processorRegister) {
        processorRegister.getOpens().add(this.open);
        processorRegister.getCreateChildAndOpens().add(this.createChildAndOpen);
        processorRegister.getReadContents().add(this.readContent);
        processorRegister.getWriteContents().add(this.writeContent);
    }

}
