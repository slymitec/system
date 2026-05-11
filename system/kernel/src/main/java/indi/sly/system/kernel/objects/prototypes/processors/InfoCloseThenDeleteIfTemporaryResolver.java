package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.lang.InfoProcessorCloseFunction;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoCloseThenDeleteIfTemporaryResolver extends AResolver implements IInfoResolver {
    public InfoCloseThenDeleteIfTemporaryResolver() {
        this.close = (info, type, cache) -> {
            if (!cache.getPath().get().isEmpty()
                    && type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.TEMPORARY) && info.getOpened() <= 0) {
                List<IdentifierDefinition> identifiers = new ArrayList<>(cache.getPath().get());
                IdentifierDefinition identifier = identifiers.removeLast();

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                InfoObject parentInfo = objectManager.get(new PathDefinition(identifiers));

                parentInfo.deleteChild(identifier);

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
