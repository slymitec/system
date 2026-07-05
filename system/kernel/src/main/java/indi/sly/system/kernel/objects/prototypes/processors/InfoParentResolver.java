package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.lang.InfoProcessorParentFunction;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.mediators.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoParentResolver extends AResolver implements IInfoResolver {
    public InfoParentResolver() {
        this.parent = (status) -> {
            List<IdentifierRecord> identifiers = new ArrayList<>(status.getPath().identifiers());
            if (identifiers.isEmpty()) {
                throw new StatusNotExistedException();
            }

            identifiers.removeLast();

            ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
            InfoObject parentInfo = objectManager.get(new PathRecord(identifiers));

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
