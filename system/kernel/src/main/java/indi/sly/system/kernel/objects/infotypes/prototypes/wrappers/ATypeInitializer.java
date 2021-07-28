package indi.sly.system.kernel.objects.infotypes.prototypes.wrappers;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import javax.inject.Named;

import indi.sly.system.common.lang.Consumer;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import indi.sly.system.kernel.objects.values.DumpDefinition;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ATypeInitializer extends APrototype {
    public abstract void install();

    public abstract void uninstall();

    public abstract UUID getPoolID(UUID id, UUID type);

    public abstract void createProcedure(InfoEntity info);

    public abstract void deleteProcedure(InfoEntity info);

    public abstract void getProcedure(InfoEntity info);

    public final void lockProcedure(InfoEntity info, long lock) {
        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolID(info.getID(), info.getType()));

        infoRepository.lock(info, lock);
    }

    public abstract void dumpProcedure(InfoEntity info, DumpDefinition dump);

    public abstract void openProcedure(InfoEntity info, InfoOpenDefinition infoOpen, long openAttribute, Object... arguments);

    public abstract void closeProcedure(InfoEntity info, InfoOpenDefinition infoOpen);

    public abstract void createChildProcedure(InfoEntity info, InfoEntity childInfo);

    public abstract InfoSummaryDefinition getChildProcedure(InfoEntity info, IdentificationDefinition identification);

    public abstract Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, Predicate<InfoSummaryDefinition> wildcard);

    public abstract void renameChildProcedure(InfoEntity info, IdentificationDefinition oldIdentification, IdentificationDefinition newIdentification);

    public abstract void deleteChildProcedure(InfoEntity info, IdentificationDefinition identification);

    protected abstract Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenDefinition infoOpen);

    public final AInfoContentObject getContentProcedure(InfoEntity info, Provider<byte[]> funcRead,
                                                        Consumer1<byte[]> funcWrite, Consumer funcExecute) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessHandleTableObject processHandleTable = process.getHandleTable();

        InfoOpenDefinition infoOpen = null;
        if (processHandleTable.containByInfoID(info.getID())) {
            ProcessHandleEntryObject processHandleTableEntry = processHandleTable.getByInfoID(info.getID());
            infoOpen = processHandleTableEntry.getOpen();
        }

        AInfoContentObject content = this.factoryManager.create(this.getContentTypeProcedure(info, infoOpen));

        content.setSource(funcRead, funcWrite);
        content.setLock((lockMode) -> this.lockProcedure(info, lockMode));
        content.setExecute(funcExecute);
        if (ObjectUtil.allNotNull(infoOpen)) {
            content.setInfoOpen(infoOpen);
        }

        return content;
    }

    public abstract void refreshPropertiesProcedure(InfoEntity info, InfoOpenDefinition infoOpen);
}
