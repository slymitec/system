package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.prototypes.processors.*;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoFactory extends AFactory {
    public InfoFactory() {
        this.infoResolvers = new CopyOnWriteArrayList<>();
    }

    protected final List<AInfoResolver> infoResolvers;

    @Override
    public void init() {
        this.infoResolvers.add(this.factoryManager.create(InfoCheckConditionResolver.class));
        this.infoResolvers.add(this.factoryManager.create(InfoDateResolver.class));
        this.infoResolvers.add(this.factoryManager.create(InfoDumpResolver.class));
        this.infoResolvers.add(this.factoryManager.create(InfoOpenOrCloseResolver.class));
        this.infoResolvers.add(this.factoryManager.create(InfoParentResolver.class));
        this.infoResolvers.add(this.factoryManager.create(InfoProcessAndThreadStatisticsResolver.class));
        this.infoResolvers.add(this.factoryManager.create(InfoProcessInfoTableResolver.class));
        this.infoResolvers.add(this.factoryManager.create(InfoSecurityDescriptorResolver.class));
        this.infoResolvers.add(this.factoryManager.create(InfoSelfResolver.class));
        this.infoResolvers.add(this.factoryManager.create(InfoTypeInitializerResolver.class));

        Collections.sort(this.infoResolvers);
    }

    public InfoObject getRootInfo() {
        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        return this.factoryManager.getCoreObjectRepository().getByHandle(SpaceType.KERNEL, kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID);
    }

    private InfoObject buildInfo(InfoProcessorMediator processorMediator, UUID infoID, InfoStatusDefinition status) {
        InfoObject info = this.factoryManager.create(InfoObject.class);

        info.factory = this;
        info.processorMediator = processorMediator;
        info.id = infoID;
        info.status = status;

        return info;
    }

    private InfoObject buildInfo(InfoEntity info, UUID poolID, InfoObject parentInfo) {
        InfoProcessorMediator processorMediator = this.factoryManager.create(InfoProcessorMediator.class);
        for (AInfoResolver infoResolver : this.infoResolvers) {
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

        status.setPoolID(poolID);

        return this.buildInfo(processorMediator, info.getID(), status);
    }

    public void buildRootInfo() {
        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository =
                memoryManager.getInfoRepository(kernelConfiguration.MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID);
        InfoEntity info =
                infoRepository.get(kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID);

        this.buildInfo(info, kernelConfiguration.MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID, null)
                .cache(SpaceType.KERNEL, kernelConfiguration.MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID);
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


    private DumpObject buildDump(InfoObject info, Provider<DumpDefinition> funcRead, Consumer1<DumpDefinition> funcWrite) {
        DumpObject dump = this.factoryManager.create(DumpObject.class);

        dump.setParent(info);
        dump.setSource(funcRead, funcWrite);

        return dump;
    }

    public DumpObject buildDump(InfoObject info, DumpDefinition dump) {
        if (ObjectUtil.isAnyNull(dump)) {
            throw new ConditionParametersException();
        }

        return this.buildDump(info, () -> dump, (source) -> {
        });
    }
}
