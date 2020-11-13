package indi.sly.system.kernel.objects;

import java.util.List;

import javax.inject.Named;

import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.objects.prototypes.StatusOpenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.StartupTypes;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.InfoObjectFactoryObject;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ObjectManager extends AManager {
    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupTypes.STEP_INIT) {

        } else if (startupTypes == StartupTypes.STEP_KERNEL) {
            InfoObjectFactoryObject infoObjectfactory = this.factoryManager.create(InfoObjectFactoryObject.class);
            infoObjectfactory.initInfoObjectFactory();

            InfoObject rootInfo = infoObjectfactory.buildRootInfoObject();
            this.factoryManager.getCoreObjectRepository().addByID(SpaceTypes.KERNEL,
                    this.factoryManager.getKernelSpace().getConfiguration().OBJECTS_PROTOTYPE_ROOT_ID, rootInfo);
        }
    }

    public InfoObject get(List<Identification> identifications) {
        if (ObjectUtils.isAnyNull(identifications) || identifications.size() > 256) {
            throw new ConditionParametersException();
        }

        InfoObject info = this.factoryManager.getCoreObjectRepository().getByID(SpaceTypes.KERNEL, InfoObject.class,
                this.factoryManager.getKernelSpace().getConfiguration().OBJECTS_PROTOTYPE_ROOT_ID);
        for (Identification identification : identifications) {
            info = info.getChild(identification);
        }

        return info;
    }

    public InfoObject rebuild(List<Identification> identifications, StatusOpenDefinition open) {
        if (ObjectUtils.isAnyNull(identifications, open)) {
            throw new ConditionParametersException();
        }

        InfoObject info;
        if (identifications.size() > 0) {
            info = this.get(identifications.subList(0, identifications.size() - 1));
            info = info.rebuildChild(identifications.get(identifications.size() - 1), open);
        } else {
            info = this.get(identifications);
        }

        return info;
    }
}
