package indi.sly.system.kernel.objects.prototypes;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.inject.Named;

import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.kernel.core.ACoreObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.processors.IInfoObjectProcessor;
import indi.sly.system.kernel.objects.types.TypeObject;
import indi.sly.system.kernel.processes.dumps.DumpDefinition;
import indi.sly.system.kernel.processes.dumps.DumpObject;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoObjectFactoryObject extends ACoreObject {
    protected Set<IInfoObjectProcessor> postProcessors;

    public void initKernelObjectFactory() {
        this.postProcessors = new ConcurrentSkipListSet<>();

        Set<ACoreObject> coreObjects = this.factoryManager.getCoreObjectRepository().getByImplementInterface(SpaceTypes.KERNEL, IInfoObjectProcessor.class);

        for (ACoreObject pair : coreObjects) {
            if (pair instanceof IInfoObjectProcessor) {
                postProcessors.add((IInfoObjectProcessor) pair);
            }
        }
    }

    public void buildRootKernelObject(InfoObject infoObject) {
        InfoObjectProcessorRegister processorRegister = new InfoObjectProcessorRegister();
        for (IInfoObjectProcessor pair : this.postProcessors) {
            pair.postProcess(null, processorRegister);
        }

        StatusDefinition status = new StatusDefinition();

        StatusOpenDefinition statusOpen = new StatusOpenDefinition();
        statusOpen.setAttribute(StatusOpenDefinitionOpenAttributeTypes.CLOSE);
        status.setOpen(statusOpen);

        infoObject.factory = this;
        infoObject.processorRegister = processorRegister;
        infoObject.id = this.factoryManager.getKernelSpace().getConfiguration().OBJECTS_PROTOTYPE_ROOT_ID;
        infoObject.poolID = this.factoryManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID;
        infoObject.status = status;
    }

    public void buildKernelObject(InfoEntity info, InfoObject parentInfoObject, InfoObject infoObject) {
        this.buildKernelObject(info, null, parentInfoObject, infoObject);
    }

    public void buildKernelObject(InfoEntity info, StatusOpenDefinition statusOpen, InfoObject parentInfoObject, InfoObject infoObject) {
        InfoObjectProcessorRegister processorRegister = new InfoObjectProcessorRegister();
        for (IInfoObjectProcessor pair : this.postProcessors) {
            pair.postProcess(info, processorRegister);
        }

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(info.getType());
        UUID poolID = type.getTypeInitializer().getPoolID(info.getID(), info.getType());

        StatusDefinition status = new StatusDefinition();
        if (ObjectUtils.isAnyNull(statusOpen)) {
            statusOpen = new StatusOpenDefinition();
            statusOpen.setAttribute(StatusOpenDefinitionOpenAttributeTypes.CLOSE);
        }
        status.setOpen(statusOpen);
        status.setParentID(parentInfoObject.getID());
        status.getIdentifications().addAll(parentInfoObject.status.getIdentifications());
        Identification identification;
        if (StringUtils.isNameIllegal(info.getName())) {
            identification = new Identification(info.getName());
        } else {
            identification = new Identification(info.getID());
        }
        status.getIdentifications().add(identification);

        infoObject.factory = this;
        infoObject.processorRegister = processorRegister;
        infoObject.id = info.getID();
        infoObject.poolID = poolID;
        infoObject.status = status;
    }

    public DumpObject buildDumpObject(DumpDefinition dumpDefinition) {
        return null;
    }
}
