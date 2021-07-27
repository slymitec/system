package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.caches.prototypes.InfoCacheObject;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.lang.CloseConsumer;
import indi.sly.system.kernel.objects.lang.OpenFunction;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OpenOrCloseResolver extends APrototype implements IInfoResolver {
    public OpenOrCloseResolver() {
        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.DO_NOT_USE_TYPE_COUNT)) {
                type.addTotalOccupiedCount();
            }

            info.setOpened(info.getOpened() + 1);
            info.setOccupied(info.getOccupied() + 1);

            return handle;
        };

        this.close = (info, type, status) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.DO_NOT_USE_TYPE_COUNT)) {
                type.minusTotalOccupiedCount();
            }

            info.setOpened(info.getOpened() - 1);
            info.setOccupied(info.getOccupied() - 1);

            if (!ValueUtil.isAnyNullOrEmpty(status.getParentID())) {
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.TEMPORARY) && info.getOpened() <= 0) {
                    IdentificationDefinition identification;
                    if (StringUtil.isNameIllegal(info.getName())) {
                        identification = new IdentificationDefinition(info.getName());
                    } else {
                        identification = new IdentificationDefinition(info.getID());
                    }

                    InfoCacheObject infoObject = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL, InfoCacheObject.class);

                    InfoObject parentInfo = infoObject.getIfExisted(SpaceType.ALL, status.getParentID());
                    parentInfo.deleteChild(identification);
                }
            }
        };
    }

    private final OpenFunction open;
    private final CloseConsumer close;

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
