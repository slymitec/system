package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeCounterObject;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.lang.InfoProcessorCloseConsumer;
import indi.sly.system.kernel.objects.lang.InfoProcessorOpenFunction;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoOpenOrCloseResolver extends APrototype implements IInfoResolver {
    public InfoOpenOrCloseResolver() {
        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.DO_NOT_USE_TYPE_COUNT)) {
                TypeCounterObject typeCount = type.getCount();
                typeCount.addTotalOccupiedCount();
            }

            info.setOpened(info.getOpened() + 1);

            return handle;
        };

        this.close = (info, type, status) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.DO_NOT_USE_TYPE_COUNT)) {
                TypeCounterObject typeCount = type.getCount();
                typeCount.minusTotalOccupiedCount();
            }

            info.setOpened(info.getOpened() - 1);

            if (status.getIdentifications().size() > 0) {
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.TEMPORARY) && info.getOpened() <= 0) {
                    List<IdentificationDefinition> identifications = new ArrayList<>(status.getIdentifications());
                    IdentificationDefinition identification = identifications.remove(identifications.size() - 1);

                    ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
                    InfoObject parentInfo = objectManager.get(identifications);

                    parentInfo.deleteChild(identification);
                }
            }
        };
    }

    private final InfoProcessorOpenFunction open;
    private final InfoProcessorCloseConsumer close;

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
