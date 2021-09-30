package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.files.instances.prototypes.FileSystemFileContentObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.instances.values.SessionType;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessContextObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessSessionObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.processes.values.ApplicationDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateContextResolver extends AProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateContextResolver() {
        this.create = (process, parentProcess, processCreator) -> {
            KernelConfigurationDefinition configuration = this.factoryManager.getKernelSpace().getConfiguration();

            ProcessContextObject processContext = process.getContext();
            ProcessSessionObject processSession = process.getSession();
            ProcessContextObject parentProcessContext = parentProcess.getContext();

            if (!ValueUtil.isAnyNullOrEmpty(processCreator.getFileIndex())) {
                ProcessInfoTableObject parentProcessInfoTable = parentProcess.getInfoTable();
                ProcessInfoEntryObject parentProcessInfoEntry = parentProcessInfoTable.getByIndex(processCreator.getFileIndex());

                InfoObject info = parentProcessInfoEntry.getInfo();
                if (!info.getType().equals(configuration.FILES_TYPES_INSTANCE_FILE_ID)) {
                    throw new StatusRelationshipErrorException();
                }

                FileSystemFileContentObject infoContent = (FileSystemFileContentObject) info.getContent();
                infoContent.execute();
                long infoContentLength = infoContent.length();
                byte[] applicationSource = infoContent.read(0, (int) infoContentLength);
                ApplicationDefinition application = ObjectUtil.transferFromString(ApplicationDefinition.class, StringUtil.readFormBytes(applicationSource));

                if (!ValueUtil.isAnyNullOrEmpty(processSession.getID())) {
                    SessionContentObject sessionContent = processSession.getContent();
                    if (ObjectUtil.allNotNull(sessionContent) && LogicalUtil.allNotEqual(sessionContent.getType(), application.getSupportedSession(), SessionType.KNOWN)) {
                        throw new StatusNotSupportedException();
                    }
                }

                processContext.setIdentifications(info.getIdentifications());
                processContext.setApplication(application);
            }

            if (!ValueUtil.isAnyNullOrEmpty(processSession.getID())) {
                SessionContentObject sessionContent = processSession.getContent();
                if (ObjectUtil.allNotNull()) {
                    processContext.setEnvironmentVariables(sessionContent.getEnvironmentVariables());
                }
            }

            if (!ValueUtil.isAnyNullOrEmpty(processCreator.getParameters())) {
                processContext.setParameters(processCreator.getParameters());
            } else {
                processContext.setParameters(StringUtil.EMPTY);
            }

            List<IdentificationDefinition> processContextWorkFolder = processCreator.getWorkFolder();
            if (ObjectUtil.allNotNull(processContextWorkFolder)) {
                ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

                try {
                    InfoObject processContextWorkFolderInfo = objectManager.get(processContextWorkFolder);

                    if (!configuration.FILES_TYPES_INSTANCE_FOLDER_ID.equals(processContextWorkFolderInfo.getType())) {
                        processContextWorkFolder = null;
                    }
                } catch (AKernelException ignored) {
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
        return 3;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(create);
    }
}
