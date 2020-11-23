package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.exceptions.*;
import indi.sly.system.common.functions.*;
import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.memory.caches.prototypes.InfoObjectCacheObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.types.prototypes.TypeObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.prototypes.PrivilegeTypes;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;
import java.util.function.Predicate;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoObject extends ACoreObject {
    protected InfoObjectFactoryObject factory;
    protected InfoObjectProcessorRegister processorRegister;

    protected UUID id;
    protected UUID poolID;
    protected InfoStatusDefinition status;

    public UUID getID() {
        if (UUIDUtils.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.id;
    }

    public UUID getParentID() {
        if (UUIDUtils.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.status.getParentID();
    }

    public UUID getType() {
        if (UUIDUtils.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.getInfo().getType();
    }

    public long getOccupied() {
        if (UUIDUtils.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.getInfo().getOccupied();
    }

    public long getOpened() {
        if (UUIDUtils.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.getInfo().getOpened();
    }

    public String getName() {
        if (UUIDUtils.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.getInfo().getName();
    }

    public UUID getHandle() {
        if (UUIDUtils.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.status.getHandle();
    }

    public List<Identification> getIdentifications() {
        if (UUIDUtils.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return Collections.unmodifiableList(this.status.getIdentifications());
    }

    private synchronized void occupy() {
        InfoEntity info = this.getInfo();
        info.setOccupied(info.getOccupied() + 1);

        InfoObject parent = this.getParent();
        if (ObjectUtils.allNotNull(parent)) {
            parent.occupy();
        }
    }

    private synchronized void free() {
        InfoEntity info = this.getInfo();
        info.setOccupied(info.getOccupied() - 1);

        InfoObject parent = this.getParent();
        if (ObjectUtils.allNotNull(parent)) {
            parent.free();
        }
    }

    private synchronized void cache(long spaceType) {
        if (UUIDUtils.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrentProcess();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        InfoObjectCacheObject kernelCache = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL,
                InfoObjectCacheObject.class);

        if (LogicalUtils.isAnyExist(spaceType, SpaceTypes.KERNEL)) {
            if (!currentProcessToken.isPrivilegeTypes(PrivilegeTypes.MEMORY_CACHE_MODIFYKERNELSPACECACHE)) {
                throw new ConditionPermissionsException();
            }

            kernelCache.add(SpaceTypes.KERNEL, this);
        }
        if (LogicalUtils.isAnyExist(spaceType, SpaceTypes.USER)) {
            kernelCache.add(SpaceTypes.USER, this);
        }
    }

    private synchronized void uncache(long spaceType) {
        if (UUIDUtils.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrentProcess();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        InfoObjectCacheObject kernelCache = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL,
                InfoObjectCacheObject.class);

        if (LogicalUtils.isAnyExist(spaceType, SpaceTypes.KERNEL)) {
            if (!currentProcessToken.isPrivilegeTypes(PrivilegeTypes.MEMORY_CACHE_MODIFYKERNELSPACECACHE)) {
                throw new ConditionPermissionsException();
            }

            kernelCache.delete(SpaceTypes.KERNEL, this.id);
        }
        if (LogicalUtils.isAnyExist(spaceType, SpaceTypes.USER)) {
            kernelCache.delete(SpaceTypes.USER, this.id);
        }
    }

    private synchronized InfoEntity getInfo() {
        return this.processorRegister.getInfo().apply(this.poolID, this.id, this.status);
    }

    public synchronized InfoObject getParent() {
        InfoEntity info = this.getInfo();

        if (UUIDUtils.isAnyNullOrEmpty(this.status.getParentID())) {
            return null;
        } else {
            return this.processorRegister.getParent().apply(this.status.getParentID());
        }
    }

    public synchronized Map<Long, Long> getDate() {
        InfoEntity info = this.getInfo();

        Map<Long, Long> date = ObjectUtils.transferFromByteArray(info.getDate());

        return Collections.unmodifiableMap(date);
    }

    public synchronized SecurityDescriptorObject getSecurityDescriptor() {
        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        Function3<SecurityDescriptorObject, InfoEntity, TypeObject, InfoStatusDefinition> func =
                this.processorRegister.getSecurityDescriptor();

        if (ObjectUtils.isAnyNull(func)) {
            throw new StatusNotSupportedException();
        }

        return func.apply(info, type, this.status);
    }

    public synchronized DumpObject dump() {
        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<Function4<DumpDefinition, DumpDefinition, InfoEntity, TypeObject, InfoStatusDefinition>> funcs =
                this.processorRegister.getDumps();

        DumpDefinition dump = new DumpDefinition();

        for (Function4<DumpDefinition, DumpDefinition, InfoEntity, TypeObject, InfoStatusDefinition> pair : funcs) {
            dump = pair.apply(dump, info, type, this.status);
        }

        return this.factory.buildDumpObject(dump);
    }

    public synchronized UUID open(long openAttribute, Object... arguments) {
        if (this.isOpened()) {
            throw new StatusAlreadyFinishedException();
        }

        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<Function6<UUID, UUID, InfoEntity, TypeObject, InfoStatusDefinition, Long, Object[]>> funcs =
                this.processorRegister.getOpens();

        UUID handle = UUIDUtils.getEmpty();

        for (Function6<UUID, UUID, InfoEntity, TypeObject, InfoStatusDefinition, Long, Object[]> pair : funcs) {
            handle = pair.apply(handle, info, type, this.status, openAttribute, arguments);
            if (ObjectUtils.isAnyNull(handle)) {
                throw new StatusUnexpectedException();
            }
        }

        this.status.getOpen().setAttribute(openAttribute);
        this.status.setHandle(handle);

        InfoObject parentInfo = this.getParent();
        if (ObjectUtils.allNotNull(parentInfo)) {
            parentInfo.occupy();
        }

        return handle;
    }

    public synchronized void close() {
        InfoEntity info = this.getInfo();

        if (!this.isOpened()) {
            throw new StatusAlreadyFinishedException();
        }

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<Consumer3<InfoEntity, TypeObject, InfoStatusDefinition>> funcs = this.processorRegister.getCloses();

        for (Consumer3<InfoEntity, TypeObject, InfoStatusDefinition> pair : funcs) {
            pair.accept(info, type, this.status);
        }

        this.status.setHandle(null);

        InfoObject parentInfo = this.getParent();
        if (ObjectUtils.allNotNull(parentInfo)) {
            parentInfo.free();
        }
    }

    public synchronized boolean isOpened() {
        return !UUIDUtils.isAnyNullOrEmpty(this.status.getHandle());
    }

    public synchronized InfoObject createChildAndOpen(UUID type, Identification identification, long openAttribute,
                                                      Object... arguments) {
        if (!UUIDUtils.isAnyNullOrEmpty(this.status.getHandle()) || ObjectUtils.isAnyNull(identification)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtils.isAnyNull(arguments)) {
            arguments = new Object[0];
        }

        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject typeObject = typeManager.get(this.getType());

        List<Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoStatusDefinition, UUID, Identification>> funcs = this.processorRegister.getCreateChildAndOpens();

        InfoEntity childInfo = null;

        for (Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoStatusDefinition, UUID, Identification> pair :
                funcs) {
            childInfo = pair.apply(childInfo, info, typeObject, this.status, type, identification);
        }

        if (ObjectUtils.isAnyNull(info)) {
            throw new StatusUnexpectedException();
        }

        InfoObject childInfoObject = this.factory.buildInfoObject(childInfo, this);

        childInfoObject.open(openAttribute, arguments);

        return childInfoObject;
    }

    public synchronized InfoObject getChild(Identification identification) {
        return this.rebuildChild(identification, null);
    }

    public synchronized InfoObject rebuildChild(Identification identification, InfoStatusOpenDefinition statusOpen) {
        if (ObjectUtils.isAnyNull(identification)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoStatusDefinition, Identification,
                InfoStatusOpenDefinition>> funcs = this.processorRegister.getGetOrRebuildChilds();

        InfoEntity childInfo = null;

        for (Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoStatusDefinition, Identification,
                InfoStatusOpenDefinition> pair : funcs) {
            childInfo = pair.apply(childInfo, info, type, this.status, identification, statusOpen);
        }

        if (ObjectUtils.isAnyNull(childInfo)) {
            throw new StatusUnexpectedException();
        }

        InfoObjectCacheObject infoObjectCache = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL,
                InfoObjectCacheObject.class);

        InfoObject childCachedInfo = infoObjectCache.getIfExisted(SpaceTypes.ALL, childInfo.getID());
        if (ObjectUtils.isAnyNull(childCachedInfo)) {
            childCachedInfo = this.factory.buildInfoObject(childInfo, statusOpen, this);

            childCachedInfo.cache(SpaceTypes.USER);
        }
        return childCachedInfo;
    }

    public synchronized void deleteChild(Identification identification) {
        if (ObjectUtils.isAnyNull(identification)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getInfo();
        InfoObject childInfo = this.getChild(identification);

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, Identification>> funcs =
                this.processorRegister.getDeleteChilds();

        for (Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, Identification> pair : funcs) {
            pair.accept(info, type, this.status, identification);
        }

        childInfo.uncache(SpaceTypes.ALL);
    }

    public synchronized Set<InfoSummaryDefinition> queryChild(Predicate<InfoSummaryDefinition> wildcard) {
        if (ObjectUtils.isAnyNull(wildcard)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<Function5<Set<InfoSummaryDefinition>, Set<InfoSummaryDefinition>, InfoEntity, TypeObject,
                InfoStatusDefinition, Predicate<InfoSummaryDefinition>>> funcs = this.processorRegister
                .getQueryChilds();

        Set<InfoSummaryDefinition> infoSummaries = new HashSet<>();

        for (Function5<Set<InfoSummaryDefinition>, Set<InfoSummaryDefinition>, InfoEntity, TypeObject,
                InfoStatusDefinition, Predicate<InfoSummaryDefinition>> pair : funcs) {
            infoSummaries = pair.apply(infoSummaries, info, type, this.status, wildcard);
        }

        return infoSummaries;
    }

    public synchronized void renameChild(Identification oldIdentification, Identification newIdentification) {
        if (ObjectUtils.isAnyNull(oldIdentification, newIdentification)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<Consumer5<InfoEntity, TypeObject, InfoStatusDefinition, Identification, Identification>> funcs =
                this.processorRegister.getRenameChilds();

        for (Consumer5<InfoEntity, TypeObject, InfoStatusDefinition, Identification, Identification> pair : funcs) {
            pair.accept(info, type, this.status, oldIdentification, newIdentification);
        }
    }

    public synchronized Map<String, String> readProperties() {
        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<Function4<Map<String, String>, Map<String, String>, InfoEntity, TypeObject, InfoStatusDefinition>> funcs =
                this.processorRegister.getReadProperties();

        Map<String, String> properties = new HashMap<>();

        for (Function4<Map<String, String>, Map<String, String>, InfoEntity, TypeObject, InfoStatusDefinition> pair :
                funcs) {
            properties = pair.apply(properties, info, type, this.status);
        }

        return Collections.unmodifiableMap(properties);
    }

    public synchronized void writeProperties(Map<String, String> properties) {
        if (ObjectUtils.isAnyNull(properties)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, Map<String, String>>> funcs =
                this.processorRegister.getWriteProperties();

        for (Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, Map<String, String>> pair : funcs) {
            pair.accept(info, type, this.status, properties);
        }
    }

    public synchronized AInfoContentObject getContent() {
        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        AInfoContentObject content = type.getTypeInitializer().getContentProcedure(info, () -> {
            List<Function4<byte[], byte[], InfoEntity, TypeObject, InfoStatusDefinition>> funcs =
                    this.processorRegister.getReadContents();

            byte[] contentSource = null;

            for (Function4<byte[], byte[], InfoEntity, TypeObject, InfoStatusDefinition> pair : funcs) {
                contentSource = pair.apply(contentSource, info, type, status);
            }

            return contentSource;
        }, (byte[] contentSource) -> {
            List<Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, byte[]>> funcs =
                    this.processorRegister.getWriteContents();

            for (Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, byte[]> pair : funcs) {
                pair.accept(info, type, status, contentSource);
            }
        }, this.status.getOpen());

        return content;
    }
}
