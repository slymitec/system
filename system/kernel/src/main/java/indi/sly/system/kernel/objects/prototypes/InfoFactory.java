package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.prototypes.processors.IInfoResolver;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoFactory extends AFactory {
    protected List<IInfoResolver> infoResolvers;

    @Override
    public void init() {
        this.infoResolvers = new CopyOnWriteArrayList<>();

        Set<APrototype> corePrototypes =
                this.factoryManager.getCorePrototypeRepository().getByImplementInterface(SpaceType.KERNEL,
                        IInfoResolver.class);

        for (APrototype corePrototype : corePrototypes) {
            if (corePrototype instanceof IInfoResolver) {
                this.infoResolvers.add((IInfoResolver) corePrototype);
            }
        }

        Collections.sort(this.infoResolvers);
    }

    public InfoObject getRootInfo() {
        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        return this.factoryManager.getCorePrototypeRepository().getByID(SpaceType.KERNEL, InfoObject.class, kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID);
    }

    private InfoObject buildInfo(InfoProcessorMediator processorMediator, UUID infoID, UUID poolID,
                                 InfoStatusDefinition status) {
        InfoObject info = this.factoryManager.create(InfoObject.class);

        info.factory = this;
        info.processorMediator = processorMediator;
        info.id = infoID;
        info.poolID = poolID;
        info.status = status;

        return info;
    }

    private InfoObject buildInfo(InfoEntity info, UUID poolID, InfoObject parentInfo) {
        InfoProcessorMediator processorMediator = this.factoryManager.create(InfoProcessorMediator.class);
        for (IInfoResolver infoResolver : this.infoResolvers) {
            infoResolver.resolve(info, processorMediator);
        }

        InfoStatusDefinition status = new InfoStatusDefinition();

        if (ObjectUtil.allNotNull(parentInfo)) {
            status.getIdentifications().addAll(parentInfo.status.getIdentifications());

            IdentificationDefinition identification;
            if (StringUtil.isNameIllegal(info.getName())) {
                identification = new IdentificationDefinition(info.getName());
            } else {
                identification = new IdentificationDefinition(info.getID());
            }
            status.getIdentifications().add(identification);
        }

        return this.buildInfo(processorMediator, info.getID(), poolID, status);
    }

    public void buildRootInfo() {
        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository =
                memoryManager.getInfoRepository(kernelConfiguration.MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID);
        InfoEntity info =
                infoRepository.get(kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID);

        this.factoryManager.getCorePrototypeRepository().addByID(SpaceType.KERNEL, kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID, this.buildInfo(info,
                kernelConfiguration.MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID, null));
    }

    public InfoObject buildInfo(InfoEntity info, InfoObject parentInfo) {
        if (ObjectUtil.isAnyNull(info, parentInfo)) {
            throw new ConditionParametersException();
        }

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(info.getType());
        UUID poolID = type.getInitializer().getPoolID(info.getID(), info.getType());

        return this.buildInfo(info, poolID, parentInfo);
    }


    public DumpObject buildDump(DumpDefinition dump) {
        DumpObject dumpObject = this.factoryManager.create(DumpObject.class);

        //

        return dumpObject;
    }
}
