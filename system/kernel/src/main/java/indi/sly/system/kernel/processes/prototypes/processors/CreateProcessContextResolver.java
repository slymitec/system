package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.lang.CreateProcessFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.prototypes.ProcessContextObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessCreatorProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessCreatorDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateProcessContextResolver extends APrototype implements IProcessCreatorResolver {
    private final CreateProcessFunction createProcessFunction;

    public CreateProcessContextResolver() {
        this.createProcessFunction = (process, parentProcess, processCreator) -> {
            KernelConfigurationDefinition configuration = this.factoryManager.getKernelSpace().getConfiguration();

            ProcessContextObject processContext = process.getContext();
            ProcessContextObject parentProcessContext = parentProcess.getContext();

            if (!ValueUtil.isAnyNullOrEmpty(processCreator.getFileHandle())) {
                //...
                processContext.setApplication(null);
            }

            if (ObjectUtil.allNotNull(processCreator.getEnvironmentVariable())) {
                processContext.setEnvironmentVariable(processCreator.getEnvironmentVariable());
            } else {
                processContext.setEnvironmentVariable(parentProcessContext.getEnvironmentVariable());
            }
            if (ObjectUtil.allNotNull(processCreator.getParameters())) {
                processContext.setParameters(processCreator.getParameters());
            } else {
                processContext.setParameters(new HashMap<>());
            }
            List<IdentificationDefinition> processContextWorkFolder = processCreator.getWorkFolder();
            if (ObjectUtil.allNotNull(processContextWorkFolder)) {
                ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

                try {
                    InfoObject processContextWorkFolderInfo = objectManager.get(processContextWorkFolder);
                    if (!configuration.FILESYSTEM_TYPES_INSTANCE_FOLDER_ID.equals(processContextWorkFolderInfo.getType())) {
                        processContextWorkFolder = null;
                    }
                } catch (AKernelException ignore) {
                    processContextWorkFolder = null;
                }
            }
            if (ObjectUtil.allNotNull(processContextWorkFolder)) {
                processContext.setWorkFolder(processContextWorkFolder);
            } else {
                processContext.setWorkFolder(parentProcessContext.getWorkFolder());
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void resolve(ProcessCreatorDefinition processCreator, ProcessCreatorProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(createProcessFunction);
    }
}
