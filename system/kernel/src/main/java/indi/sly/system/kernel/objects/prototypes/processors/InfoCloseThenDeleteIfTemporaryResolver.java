package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.lang.InfoProcessorCloseFunction;
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
public class InfoCloseThenDeleteIfTemporaryResolver extends AInfoResolver {
    public InfoCloseThenDeleteIfTemporaryResolver() {
        this.close = (info, type, status) -> {
            if (!status.getIdentifications().isEmpty()
                    && type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.TEMPORARY) && info.getOpened() <= 0) {
                List<IdentificationDefinition> identifications = new ArrayList<>(status.getIdentifications());
                IdentificationDefinition identification = identifications.remove(identifications.size() - 1);

                ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
                InfoObject parentInfo = objectManager.get(identifications);

                parentInfo.deleteChild(identification);

                info = null;
            }

            return info;

        };
    }

    private final InfoProcessorCloseFunction close;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getCloses().add(this.close);
    }

    @Override
    public int order() {
        return 4;
    }
}
