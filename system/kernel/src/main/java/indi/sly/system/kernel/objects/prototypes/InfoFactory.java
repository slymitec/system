package indi.sly.system.kernel.objects.prototypes;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.inject.Named;

import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusOpenAttributeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.processors.IInfoResolver;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoFactory extends APrototype {
    protected Set<IInfoResolver> infoResolvers;

    public void init() {
        this.infoResolvers = new ConcurrentSkipListSet<>();

        Set<APrototype> corePrototypes =
                this.factoryManager.getCoreRepository().getByImplementInterface(SpaceType.KERNEL,
                        IInfoResolver.class);

        for (APrototype pair : corePrototypes) {
            if (pair instanceof IInfoResolver) {
                infoResolvers.add((IInfoResolver) pair);
            }
        }
    }

    public InfoObject buildRootInfoObject() {
        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository =
                memoryManager.getInfoRepository(this.factoryManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID);
        InfoEntity infoEntity =
                infoRepository.get(this.factoryManager.getKernelSpace().getConfiguration().OBJECTS_PROTOTYPE_ROOT_ID);

        InfoProcessorMediator processorRegister = new InfoProcessorMediator();
        for (IInfoResolver pair : this.infoResolvers) {
            pair.process(infoEntity, processorRegister);
        }

        InfoStatusOpenDefinition statusOpen = new InfoStatusOpenDefinition();
        statusOpen.setAttribute(InfoStatusOpenAttributeType.CLOSE);
        InfoStatusDefinition status = new InfoStatusDefinition();
        status.setOpen(statusOpen);

        InfoObject infoObject = this.factoryManager.create(InfoObject.class);

        infoObject.factory = this;
        infoObject.processorMediator = processorRegister;
        infoObject.id = this.factoryManager.getKernelSpace().getConfiguration().OBJECTS_PROTOTYPE_ROOT_ID;
        infoObject.poolID =
                this.factoryManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID;
        infoObject.status = status;

        return infoObject;
    }

    public InfoObject buildInfoObject(InfoEntity info, InfoObject parentInfo) {
        return this.buildInfoObject(info, null, parentInfo);
    }

    public InfoObject buildInfoObject(InfoEntity info, InfoStatusOpenDefinition statusOpen, InfoObject parentInfo) {
        InfoProcessorMediator infoProcessorMediator = new InfoProcessorMediator();
        for (IInfoResolver infoObjectProcessor : this.infoResolvers) {
            infoObjectProcessor.process(info, infoProcessorMediator);
        }

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(info.getType());
        UUID poolID = type.getTypeInitializer().getPoolID(info.getID(), info.getType());

        InfoStatusDefinition status = new InfoStatusDefinition();
        if (ObjectUtil.isAnyNull(statusOpen)) {
            statusOpen = new InfoStatusOpenDefinition();
            statusOpen.setAttribute(InfoStatusOpenAttributeType.CLOSE);
        }
        status.setOpen(statusOpen);
        status.setParentID(parentInfo.getID());
        status.getIdentifications().addAll(parentInfo.status.getIdentifications());
        IdentificationDefinition identification;
        if (StringUtil.isNameIllegal(info.getName())) {
            identification = new IdentificationDefinition(info.getName());
        } else {
            identification = new IdentificationDefinition(info.getID());
        }
        status.getIdentifications().add(identification);

        InfoObject infoObject = this.factoryManager.create(InfoObject.class);

        infoObject.factory = this;
        infoObject.processorMediator = infoProcessorMediator;
        infoObject.id = info.getID();
        infoObject.poolID = poolID;
        infoObject.status = status;

        return infoObject;
    }

    public DumpObject buildDumpObject(DumpDefinition dump) {
        DumpObject dumpObject = this.factoryManager.create(DumpObject.class);

        //


        return null;
    }
}
