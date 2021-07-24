package indi.sly.system.kernel.objects.infotypes.prototypes.wrappers;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import javax.inject.Named;

import indi.sly.system.common.lang.Consumer;
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
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;
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

    public abstract void openProcedure(InfoEntity info, InfoStatusOpenDefinition statusOpen, long openAttribute, Object... arguments);

    public abstract void closeProcedure(InfoEntity info, InfoStatusOpenDefinition statusOpen);

    public abstract void createChildProcedure(InfoEntity info, InfoEntity childInfo);

    public abstract InfoSummaryDefinition getChildProcedure(InfoEntity info, IdentificationDefinition identification);

    public abstract Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, Predicate<InfoSummaryDefinition> wildcard);

    public abstract void renameChildProcedure(InfoEntity info, IdentificationDefinition oldIdentification, IdentificationDefinition newIdentification);

    public abstract void deleteChildProcedure(InfoEntity info, IdentificationDefinition identification);

    protected abstract Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoStatusOpenDefinition statusOpen);

    public final AInfoContentObject getContentProcedure(InfoEntity info, Provider<byte[]> funcRead,
                                                        Consumer1<byte[]> funcWrite, Consumer funcExecute,
                                                        InfoStatusOpenDefinition statusOpen) {
        AInfoContentObject content = this.factoryManager.create(this.getContentTypeProcedure(info, statusOpen));

        content.setSource(funcRead, funcWrite);
        content.setLock((lockMode) -> this.lockProcedure(info, lockMode));
        content.setExecute(funcExecute);
        content.setStatusOpen(statusOpen);

        return content;
    }

    public abstract void refreshPropertiesProcedure(InfoEntity info, InfoStatusOpenDefinition statusOpen);
}
