package indi.sly.system.kernel.objects.prototypes;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.inject.Named;

import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;
import indi.sly.system.kernel.objects.types.InfoStatusOpenAttributeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.processors.IInfoObjectProcessor;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoFactory extends ACorePrototype {
    protected Set<IInfoObjectProcessor> infoObjectProcessors;

    public void initInfoObjectFactory() {
        this.infoObjectProcessors = new ConcurrentSkipListSet<>();

        Set<ACorePrototype> coreObjects =
                this.factoryManager.getCoreRepository().getByImplementInterface(SpaceTypes.KERNEL,
                        IInfoObjectProcessor.class);

        for (ACorePrototype pair : coreObjects) {
            if (pair instanceof IInfoObjectProcessor) {
                infoObjectProcessors.add((IInfoObjectProcessor) pair);
            }
        }
    }

    public InfoObject buildRootInfoObject() {
        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository =
                memoryManager.getInfoRepository(this.factoryManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID);
        InfoEntity infoEntity =
                infoRepository.get(this.factoryManager.getKernelSpace().getConfiguration().OBJECTS_PROTOTYPE_ROOT_ID);

        InfoProcessorRegister processorRegister = new InfoProcessorRegister();
        for (IInfoObjectProcessor pair : this.infoObjectProcessors) {
            pair.process(infoEntity, processorRegister);
        }

        InfoStatusOpenDefinition statusOpen = new InfoStatusOpenDefinition();
        statusOpen.setAttribute(InfoStatusOpenAttributeTypes.CLOSE);
        InfoStatusDefinition status = new InfoStatusDefinition();
        status.setOpen(statusOpen);

        InfoObject infoObject = this.factoryManager.create(InfoObject.class);

        infoObject.factory = this;
        infoObject.processorRegister = processorRegister;
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
        InfoProcessorRegister infoProcessorRegister = new InfoProcessorRegister();
        for (IInfoObjectProcessor infoObjectProcessor : this.infoObjectProcessors) {
            infoObjectProcessor.process(info, infoProcessorRegister);
        }

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(info.getType());
        UUID poolID = type.getTypeInitializer().getPoolID(info.getID(), info.getType());

        InfoStatusDefinition status = new InfoStatusDefinition();
        if (ObjectUtils.isAnyNull(statusOpen)) {
            statusOpen = new InfoStatusOpenDefinition();
            statusOpen.setAttribute(InfoStatusOpenAttributeTypes.CLOSE);
        }
        status.setOpen(statusOpen);
        status.setParentID(parentInfo.getID());
        status.getIdentifications().addAll(parentInfo.status.getIdentifications());
        Identification identification;
        if (StringUtils.isNameIllegal(info.getName())) {
            identification = new Identification(info.getName());
        } else {
            identification = new Identification(info.getID());
        }
        status.getIdentifications().add(identification);

        InfoObject infoObject = this.factoryManager.create(InfoObject.class);

        infoObject.factory = this;
        infoObject.processorRegister = infoProcessorRegister;
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