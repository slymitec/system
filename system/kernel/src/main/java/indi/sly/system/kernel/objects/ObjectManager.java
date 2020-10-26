package indi.sly.system.kernel.objects;

import java.util.List;

import javax.inject.Named;

import indi.sly.system.kernel.core.enviroment.SpaceTypes;
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
            InfoObjectFactoryObject factory = this.factoryManager.create(InfoObjectFactoryObject.class);
            factory.initKernelObjectFactory();

            InfoObject rootInfoObject = this.factoryManager.create(InfoObject.class);
            factory.buildRootKernelObject(rootInfoObject);

            this.factoryManager.getCoreObjectRepository().addByID(SpaceTypes.KERNEL, this.factoryManager.getKernelSpace().getConfiguration().OBJECTS_PROTOTYPE_ROOT_ID, rootInfoObject);
        }
    }

    public InfoObject get(List<Identification> identifications) {
        if (ObjectUtils.isAnyNull(identifications) || identifications.size() > 256) {
            throw new ConditionParametersException();
        }

        InfoObject resultInfoObject = this.factoryManager.getCoreObjectRepository().getByID(SpaceTypes.KERNEL, InfoObject.class, this.factoryManager.getKernelSpace().getConfiguration().OBJECTS_PROTOTYPE_ROOT_ID);

        for (Identification identification : identifications) {
            resultInfoObject = resultInfoObject.getChild(identification);
        }

        return resultInfoObject;
    }
}
