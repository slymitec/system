package indi.sly.system.kernel.objects.infotypes.prototypes.processors;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.processors.AInitializer;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.values.*;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

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

    public abstract UUID getPoolId(UUID id, UUID type);

    public void createProcedure(InfoEntity info) {
    }

    public void deleteProcedure(InfoEntity info) {
    }

    public void getProcedure(InfoEntity info, IdentifierRecord identification) {
    }

    public final void lockProcedure(InfoEntity info, long lock) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

        infoRepository.lock(info, lock);
    }

    public final void unlockProcedure(InfoEntity info, long lock) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(this.getPoolId(info.getId(), info.getType()));

        infoRepository.unlock(info, lock);
    }

    public void dumpProcedure(InfoEntity info, DumpCacheEntity dump) {
    }

    public void openProcedure(InfoEntity info, InfoOpenRecord infoOpen, Object[] arguments) {
    }

    public void closeProcedure(InfoEntity info, InfoOpenRecord infoOpen) {
    }

    public void createChildProcedure(InfoEntity info, InfoEntity childInfo) {
        throw new StatusNotSupportedException();
    }

    public InfoSummaryDefinition getChildProcedure(InfoEntity info, IdentifierRecord identification) {
        throw new StatusNotSupportedException();
    }

    public Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, InfoWildcardRecord wildcard) {
        throw new StatusNotSupportedException();
    }

    public void renameChildProcedure(InfoEntity info, IdentifierRecord oldIdentification,
                                     IdentifierRecord newIdentification) {
        throw new StatusNotSupportedException();
    }

    public void deleteChildProcedure(InfoEntity info, IdentifierRecord identification) {
        throw new StatusNotSupportedException();
    }

    public Map<String, String> readPropertiesProcedure(InfoEntity info, InfoOpenRecord infoOpen) {
        try {
            this.lockProcedure(info, LockType.READ);

            return info.getProperties();
        } finally {
            this.unlockProcedure(info, LockType.READ);
        }
    }

    public void writePropertiesProcedure(InfoEntity info, Map<String, String> properties, InfoOpenRecord infoOpen) {
        try {
            this.lockProcedure(info, LockType.WRITE);

            info.setProperties(properties);
        } finally {
            this.unlockProcedure(info, LockType.WRITE);
        }
    }

    public abstract Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenRecord infoOpen);

    public byte[] readContentProcedure(InfoEntity info, InfoOpenRecord infoOpen) {
        try {
            this.lockProcedure(info, LockType.READ);

            return info.getContent();
        } finally {
            this.unlockProcedure(info, LockType.READ);
        }
    }

    public void writeContentProcedure(InfoEntity info, InfoOpenRecord infoOpen, byte[] source) {
        if (source.length > 4096) {
            throw new StatusOverflowException();
        }

        try {
            this.lockProcedure(info, LockType.WRITE);

            info.setContent(source);
        } finally {
            this.unlockProcedure(info, LockType.WRITE);
        }
    }

    public void executeContentProcedure(InfoEntity info, InfoOpenRecord infoOpen) {
    }
}
