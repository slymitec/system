package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.lang.InfoProcessorParentFunction;
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
public class InfoParentResolver extends AInfoResolver {
    public InfoParentResolver() {
        this.parent = (status) -> {
            List<IdentificationDefinition> identifications = new ArrayList<>(status.getIdentifications());
            if (identifications.size() == 0) {
                throw new StatusNotExistedException();
            }

            identifications.remove(identifications.size() - 1);

            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
            InfoObject parentInfo = objectManager.get(identifications);

            return parentInfo;
        };
    }

    private final InfoProcessorParentFunction parent;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.setParent(this.parent);
    }

    @Override
    public int order() {
        return 0;
    }
}
