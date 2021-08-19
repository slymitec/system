package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.objects.lang.InfoProcessorCreateChildAndOpenFunction;
import indi.sly.system.kernel.objects.lang.InfoProcessorOpenFunction;
import indi.sly.system.kernel.objects.lang.InfoProcessorReadContentFunction;
import indi.sly.system.kernel.objects.lang.InfoProcessorWriteContentConsumer;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoDateResolver extends AInfoResolver {
    public InfoDateResolver() {
        this.open = (index, info, type, status, openAttribute, arguments) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = ObjectUtil.transferFromByteArray(info.getDate());
            assert date != null;
            date.put(DateTimeType.ACCESS, nowDateTime);
            info.setDate(ObjectUtil.transferToByteArray(date));

            return index;
        };

        this.createChildAndOpen = (childInfo, info, type, status, childType, identification) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = new HashMap<>();
            date.put(DateTimeType.CREATE, nowDateTime);
            date.put(DateTimeType.MODIFIED, nowDateTime);
            date.put(DateTimeType.ACCESS, nowDateTime);
            childInfo.setDate(ObjectUtil.transferToByteArray(date));

            return childInfo;
        };

        this.readContent = (content, info, type, status) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = ObjectUtil.transferFromByteArray(info.getDate());
            assert date != null;
            date.put(DateTimeType.ACCESS, nowDateTime);
            info.setDate(ObjectUtil.transferToByteArray(date));

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = ObjectUtil.transferFromByteArray(info.getDate());
            assert date != null;
            date.put(DateTimeType.MODIFIED, nowDateTime);
            info.setDate(ObjectUtil.transferToByteArray(date));
        };
    }

    private final InfoProcessorOpenFunction open;
    private final InfoProcessorCreateChildAndOpenFunction createChildAndOpen;
    private final InfoProcessorReadContentFunction readContent;
    private final InfoProcessorWriteContentConsumer writeContent;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getOpens().add(this.open);
        processorMediator.getCreateChildAndOpens().add(this.createChildAndOpen);
        processorMediator.getReadContents().add(this.readContent);
        processorMediator.getWriteContents().add(this.writeContent);
    }

    @Override
    public int order() {
        return 3;
    }
}
