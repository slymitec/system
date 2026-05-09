package indi.sly.system.kernel.objects.infotypes.prototypes.processors;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentifierDefinition;
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

    public void getProcedure(InfoEntity info, IdentifierDefinition identification) {
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

    public void openProcedure(InfoEntity info, InfoOpenDefinition infoOpen, Object[] arguments) {
    }

    public void closeProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
    }

    public void createChildProcedure(InfoEntity info, InfoEntity childInfo) {
        throw new StatusNotSupportedException();
    }

    public InfoSummaryDefinition getChildProcedure(InfoEntity info, IdentifierDefinition identification) {
        throw new StatusNotSupportedException();
    }

    public Set<InfoSummaryDefinition> queryChildProcedure(InfoEntity info, InfoWildcardDefinition wildcard) {
        throw new StatusNotSupportedException();
    }

    public void renameChildProcedure(InfoEntity info, IdentifierDefinition oldIdentification,
                                     IdentifierDefinition newIdentification) {
        throw new StatusNotSupportedException();
    }

    public void deleteChildProcedure(InfoEntity info, IdentifierDefinition identification) {
        throw new StatusNotSupportedException();
    }

    public Map<String, String> readPropertiesProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        try {
            this.lockProcedure(info, LockType.READ);

            return ObjectUtil.transferFromByteArray(info.getProperties());
        } finally {
            this.unlockProcedure(info, LockType.READ);
        }
    }

    public void writePropertiesProcedure(InfoEntity info, Map<String, String> properties, InfoOpenDefinition infoOpen) {
        byte[] propertiesSource = ObjectUtil.transferToByteArray(properties);

        if (propertiesSource.length > 1024) {
            throw new StatusOverflowException();
        }

        try {
            this.lockProcedure(info, LockType.WRITE);

            info.setProperties(propertiesSource);
        } finally {
            this.unlockProcedure(info, LockType.WRITE);
        }
    }

    public abstract Class<? extends AInfoContentObject> getContentTypeProcedure(InfoEntity info, InfoOpenDefinition infoOpen);

    public byte[] readContentProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
        try {
            this.lockProcedure(info, LockType.READ);

            return info.getContent();
        } finally {
            this.unlockProcedure(info, LockType.READ);
        }
    }

    public void writeContentProcedure(InfoEntity info, InfoOpenDefinition infoOpen, byte[] source) {
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

    public void executeContentProcedure(InfoEntity info, InfoOpenDefinition infoOpen) {
    }
}
