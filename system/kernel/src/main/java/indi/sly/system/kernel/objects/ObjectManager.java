package indi.sly.system.kernel.objects;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoFactory;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ObjectManager extends AManager {
    private InfoFactory factory;

    public InfoFactory getFactory() {
        return this.factory;
    }

    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
            this.factory = this.coreManager.create(InfoFactory.class);
            this.factory.init();
        } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
            this.factory.buildRootInfo();
        }
    }

    @Override
    public void shutdown() {
    }

    public InfoObject get(PathDefinition path) {
        KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

        if (ObjectUtil.isAnyNull(path) || path.get().size() > kernelConfiguration.OBJECTS_INFO_PATH_MAX_DEPTH) {
            throw new ConditionParametersException();
        }

        InfoObject info = this.factory.getRootInfo();

        for (IdentifierDefinition identifier : path.get()) {
            info = info.getChild(identifier);
        }

        return info;
    }

    //--
    public InfoObject get(List<IdentifierDefinition> identifiers) {
        if (ObjectUtil.isAnyNull(identifiers) || identifiers.size() > 256) {
            throw new ConditionParametersException();
        }

        InfoObject info = this.factory.getRootInfo();

        for (IdentifierDefinition identifier : identifiers) {
            info = info.getChild(identifier);
        }

        return info;
    }
}
