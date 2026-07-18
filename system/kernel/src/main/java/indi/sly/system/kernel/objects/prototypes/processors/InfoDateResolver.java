package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.values.DateTimeType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.objects.lang.InfoProcessorCreateChildFunction;
import indi.sly.system.kernel.objects.lang.InfoProcessorOpenFunction;
import indi.sly.system.kernel.objects.lang.InfoProcessorReadContentFunction;
import indi.sly.system.kernel.objects.lang.InfoProcessorWriteContentConsumer;
import indi.sly.system.kernel.objects.prototypes.mediators.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoDateResolver extends AResolver implements IInfoResolver {
    public InfoDateResolver() {
        this.open = (index, info, type, cache, openAttribute, arguments) -> {
            DateTimeObject dateTime = this.coreManager.getDateTime();
            long nowDateTime = dateTime.getCurrent();

            Map<Long, Long> date = info.getDate();
            assert date != null;
            date.put(DateTimeType.ACCESS, nowDateTime);

            info.setDate(date);

            return index;
        };

        this.createChild = (childInfo, info, type, cache, childType, identification) -> {
            DateTimeObject dateTime = this.coreManager.getDateTime();
            long nowDateTime = dateTime.getCurrent();

            Map<Long, Long> date = childInfo.getDate();
            assert date != null;
            date.put(DateTimeType.CREATE, nowDateTime);
            date.put(DateTimeType.MODIFIED, nowDateTime);
            date.put(DateTimeType.ACCESS, nowDateTime);

            childInfo.setDate(date);

            return childInfo;
        };

        this.readContent = (content, info, type, cache) -> {
            DateTimeObject dateTime = this.coreManager.getDateTime();
            long nowDateTime = dateTime.getCurrent();

            Map<Long, Long> date = info.getDate();
            assert date != null;
            date.put(DateTimeType.ACCESS, nowDateTime);

            info.setDate(date);

            return content;
        };

        this.writeContent = (info, type, cache, content) -> {
            DateTimeObject dateTime = this.coreManager.getDateTime();
            long nowDateTime = dateTime.getCurrent();

            Map<Long, Long> date = info.getDate();
            assert date != null;
            date.put(DateTimeType.MODIFIED, nowDateTime);

            info.setDate(date);
        };
    }

    private final InfoProcessorOpenFunction open;
    private final InfoProcessorCreateChildFunction createChild;
    private final InfoProcessorReadContentFunction readContent;
    private final InfoProcessorWriteContentConsumer writeContent;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getOpens().add(this.open);
        processorMediator.getCreateChildren().add(this.createChild);
        processorMediator.getReadContents().add(this.readContent);
        processorMediator.getWriteContents().add(this.writeContent);
    }

    @Override
    public int order() {
        return 3;
    }
}
