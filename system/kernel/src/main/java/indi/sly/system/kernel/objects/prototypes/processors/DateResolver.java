package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.Consumer4;
import indi.sly.system.common.lang.Function4;
import indi.sly.system.common.lang.Function6;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
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
public class DateResolver extends APrototype implements IInfoObjectResolver {
    public DateResolver() {
        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = ObjectUtil.transferFromByteArray(info.getDate());
            date.put(DateTimeTypes.ACCESS, nowDateTime);
            info.setDate(ObjectUtil.transferToByteArray(date));

            return handle;
        };

        this.createChildAndOpen = (childInfo, info, type, status, childType, identification) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = new HashMap<>();
            date.put(DateTimeTypes.CREATE, nowDateTime);
            date.put(DateTimeTypes.MODIFIED, nowDateTime);
            date.put(DateTimeTypes.ACCESS, nowDateTime);
            childInfo.setDate(ObjectUtil.transferToByteArray(date));

            return childInfo;
        };

        this.readContent = (content, info, type, status) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = ObjectUtil.transferFromByteArray(info.getDate());
            date.put(DateTimeTypes.ACCESS, nowDateTime);
            info.setDate(ObjectUtil.transferToByteArray(date));

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                    DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = ObjectUtil.transferFromByteArray(info.getDate());
            date.put(DateTimeTypes.MODIFIED, nowDateTime);
            info.setDate(ObjectUtil.transferToByteArray(date));
        };
    }

    private final OpenFunction open;
    private final CreateChildAndOpenFunction createChildAndOpen;
    private final ReadContentFunction readContent;
    private final WriteContentConsumer writeContent;

    @Override
    public void process(InfoEntity info, InfoProcessorMediator processorRegister) {
        processorRegister.getOpens().add(this.open);
        processorRegister.getCreateChildAndOpens().add(this.createChildAndOpen);
        processorRegister.getReadContents().add(this.readContent);
        processorRegister.getWriteContents().add(this.writeContent);
    }
}
