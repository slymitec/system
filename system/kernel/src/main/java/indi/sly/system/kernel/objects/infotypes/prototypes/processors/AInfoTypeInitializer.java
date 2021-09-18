package indi.sly.system.kernel.objects.infotypes.prototypes.processors;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.prototypes.processors.AInitializer;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.lang.InfoQueryChildPredicate;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AInfoTypeInitializer extends AInitializer {
    public void install() {
    }

    public void uninstall() {
    }

    public abstract UUID getPoolID(UUID id, UUID type);

    public void createProcedure(InfoEntity info) {
    }

    public void deleteProcedure(InfoEntity info) {
    }

    public void getProcedure(InfoEntity info, IdentificationDefinition identification) {
    }

    public final void lockProcedure(InfoEntity info, long lock) {
        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(), info.getType()));

        infoRepository.lock(info, lock);
    }

    public void dumpProcedure(InfoEntity info, DumpDefinition dump) {
    }

    public void openProcedure(InfoEntity info, InfoOpenDefinition infoOpen, Object[] arguments) {
    }

    public void closeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
    }

    public void createChildProcedure(InfoEntity info, InfoEntity childInfo) {
        throw new StatusNotSupportedException();
    }

    public InfoSummaryDefinition getChildProcedure(InfoEntity info, IdentificationDefinition identification) {
        throw new StatusNotSupportedException();
    }

    public Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, InfoQueryChildPredicate wildcard) {
        throw new StatusNotSupportedException();
    }

    public void renameChildProcedure(InfoEntity info, IdentificationDefinition oldIdentification,
                                     IdentificationDefinition newIdentification) {
        throw new StatusNotSupportedException();
    }

    public void deleteChildProcedure(InfoEntity info, IdentificationDefinition identification) {
        throw new StatusNotSupportedException();
    }

    public Map<String, String> readPropertiesProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        Map<String, String> properties = ObjectUtil.transferFromByteArray(info.getProperties());
        assert properties != null;

        return properties;
    }

    public void writePropertiesProcedure(InfoEntity info, Map<String, String> properties, InfoOpenDefinition infoOpen) {
        byte[] propertiesSource = ObjectUtil.transferToByteArray(properties);
        assert propertiesSource != null;

        if (propertiesSource.length > 1024) {
            throw new StatusOverflowException();
        }

        info.setProperties(propertiesSource);
    }

    protected abstract Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenDefinition infoOpen);

    public final AInfoContentObject getContentProcedure(Provider<InfoEntity> infoProvider, Provider<byte[]> funcRead,
                                                        Consumer1<byte[]> funcWrite, Consumer funcExecute) {
        InfoEntity info = infoProvider.acquire();

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();

        InfoOpenDefinition infoOpen = null;
        if (processInfoTable.containByID(info.getID())) {
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(info.getID());
            infoOpen = processInfoEntry.getOpen();
        }

        AInfoContentObject content = this.factoryManager.create(this.getContentTypeProcedure(info, infoOpen));

        content.setSource(funcRead, funcWrite);
        content.setLock((lockMode) -> this.lockProcedure(infoProvider.acquire(), lockMode));
        content.setExecute(funcExecute);
        if (ObjectUtil.allNotNull(infoOpen)) {
            content.setInfoOpen(infoOpen);
        }

        return content;
    }

    public byte[] readContentProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        return info.getContent();
    }

    public void writeContentProcedure(InfoEntity info, InfoOpenDefinition infoOpen, byte[] source) {
        if (source.length > 4096) {
            throw new StatusOverflowException();
        }

        info.setContent(source);
    }

    public void executeContentProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
    }
}
