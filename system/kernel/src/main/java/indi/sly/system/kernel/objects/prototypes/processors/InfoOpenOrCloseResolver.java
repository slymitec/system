package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.objects.infotypes.prototypes.TypeCounterObject;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.lang.InfoProcessorCloseFunction;
import indi.sly.system.kernel.objects.lang.InfoProcessorOpenFunction;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoOpenOrCloseResolver extends AInfoResolver {
    public InfoOpenOrCloseResolver() {
        this.open = (index, info, type, status, openAttribute, arguments) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.DO_NOT_USE_TYPE_COUNT)) {
                TypeCounterObject typeCount = type.getCount();
                typeCount.addTotalOccupiedCount();
            }

            info.setOpened(info.getOpened() + 1);

            return index;
        };

        this.close = (info, type, status) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.DO_NOT_USE_TYPE_COUNT)) {
                TypeCounterObject typeCount = type.getCount();
                typeCount.minusTotalOccupiedCount();
            }

            info.setOpened(info.getOpened() - 1);

            return info;
        };
    }

    private final InfoProcessorOpenFunction open;
    private final InfoProcessorCloseFunction close;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getOpens().add(this.open);
        processorMediator.getCloses().add(this.close);
    }

    @Override
    public int order() {
        return 1;
    }
}
