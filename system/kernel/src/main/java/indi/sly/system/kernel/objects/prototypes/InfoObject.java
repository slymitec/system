package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.caches.prototypes.InfoCacheObject;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;
import java.util.function.Predicate;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoObject extends APrototype {
    protected InfoFactory factory;
    protected InfoProcessorMediator processorRegister;

    protected UUID id;
    protected UUID poolID;
    protected InfoStatusDefinition status;

    public UUID getID() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.id;
    }

    public UUID getParentID() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.status.getParentID();
    }

    public UUID getType() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.getInfo().getType();
    }

    public long getOccupied() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.getInfo().getOccupied();
    }

    public long getOpened() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.getInfo().getOpened();
    }

    public String getName() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.getInfo().getName();
    }

    public UUID getHandle() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.status.getHandle();
    }

    public List<IdentificationDefinition> getIdentifications() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return Collections.unmodifiableList(this.status.getIdentifications());
    }

    private synchronized void occupy() {
        InfoEntity info = this.getInfo();
        info.setOccupied(info.getOccupied() + 1);

        InfoObject parent = this.getParent();
        if (ObjectUtil.allNotNull(parent)) {
            parent.occupy();
        }
    }

    private synchronized void free() {
        InfoEntity info = this.getInfo();
        info.setOccupied(info.getOccupied() - 1);

        InfoObject parent = this.getParent();
        if (ObjectUtil.allNotNull(parent)) {
            parent.free();
        }
    }

    private synchronized void cache(long spaceType) {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrentProcess();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        InfoCacheObject kernelCache = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                InfoCacheObject.class);

        if (LogicalUtil.isAnyExist(spaceType, SpaceType.KERNEL)) {
            if (!currentProcessToken.isPrivilegeTypes(PrivilegeTypes.MEMORY_CACHE_MODIFYKERNELSPACECACHE)) {
                throw new ConditionPermissionsException();
            }

            kernelCache.add(SpaceType.KERNEL, this);
        }
        if (LogicalUtil.isAnyExist(spaceType, SpaceType.USER)) {
            kernelCache.add(SpaceType.USER, this);
        }
    }

    private synchronized void uncache(long spaceType) {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrentProcess();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        InfoCacheObject kernelCache = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                InfoCacheObject.class);

        if (LogicalUtil.isAnyExist(spaceType, SpaceType.KERNEL)) {
            if (!currentProcessToken.isPrivilegeTypes(PrivilegeTypes.MEMORY_CACHE_MODIFYKERNELSPACECACHE)) {
                throw new ConditionPermissionsException();
            }

            kernelCache.delete(SpaceType.KERNEL, this.id);
        }
        if (LogicalUtil.isAnyExist(spaceType, SpaceType.USER)) {
            kernelCache.delete(SpaceType.USER, this.id);
        }
    }

    private synchronized InfoEntity getInfo() {
        return this.processorRegister.getInfo().apply(this.poolID, this.id, this.status);
    }

    public synchronized InfoObject getParent() {
        InfoEntity info = this.getInfo();

        if (ValueUtil.isAnyNullOrEmpty(this.status.getParentID())) {
            return null;
        } else {
            return this.processorRegister.getParent().apply(this.status.getParentID());
        }
    }

    public synchronized Map<Long, Long> getDate() {
        InfoEntity info = this.getInfo();

        Map<Long, Long> date = ObjectUtil.transferFromByteArray(info.getDate());

        return Collections.unmodifiableMap(date);
    }

    public synchronized SecurityDescriptorObject getSecurityDescriptor() {
        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        Function3<SecurityDescriptorObject, InfoEntity, TypeObject, InfoStatusDefinition> func =
                this.processorRegister.getSecurityDescriptor();

        if (ObjectUtil.isAnyNull(func)) {
            throw new StatusNotSupportedException();
        }

        return func.apply(info, type, this.status);
    }

    public synchronized DumpObject dump() {
        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<DumpFunction> funcs = this.processorRegister.getDumps();

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

        List<OpenFunction> funcs = this.processorRegister.getOpens();

        UUID handle = UUIDUtil.getEmpty();

        for (Function6<UUID, UUID, InfoEntity, TypeObject, InfoStatusDefinition, Long, Object[]> pair : funcs) {
            handle = pair.apply(handle, info, type, this.status, openAttribute, arguments);
            if (ObjectUtil.isAnyNull(handle)) {
                throw new StatusUnexpectedException();
            }
        }

        this.status.getOpen().setAttribute(openAttribute);
        this.status.setHandle(handle);

        InfoObject parentInfo = this.getParent();
        if (ObjectUtil.allNotNull(parentInfo)) {
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

        List<CloseConsumer> funcs = this.processorRegister.getCloses();

        for (Consumer3<InfoEntity, TypeObject, InfoStatusDefinition> pair : funcs) {
            pair.accept(info, type, this.status);
        }

        this.status.setHandle(null);

        InfoObject parentInfo = this.getParent();
        if (ObjectUtil.allNotNull(parentInfo)) {
            parentInfo.free();
        }
    }

    public synchronized boolean isOpened() {
        return !ValueUtil.isAnyNullOrEmpty(this.status.getHandle());
    }

    public synchronized InfoObject createChildAndOpen(UUID type, IdentificationDefinition identification,
                                                      long openAttribute, Object... arguments) {
        if (!ValueUtil.isAnyNullOrEmpty(this.status.getHandle()) || ObjectUtil.isAnyNull(identification)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(arguments)) {
            arguments = new Object[0];
        }

        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject typeObject = typeManager.get(this.getType());

        List<CreateChildAndOpenFunction> funcs = this.processorRegister.getCreateChildAndOpens();

        InfoEntity childInfo = null;

        for (Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoStatusDefinition, UUID,
                IdentificationDefinition> pair :
                funcs) {
            childInfo = pair.apply(childInfo, info, typeObject, this.status, type, identification);
        }

        if (ObjectUtil.isAnyNull(info)) {
            throw new StatusUnexpectedException();
        }

        InfoObject childInfoObject = this.factory.buildInfoObject(childInfo, this);

        childInfoObject.open(openAttribute, arguments);

        return childInfoObject;
    }

    public synchronized InfoObject getChild(IdentificationDefinition identification) {
        return this.rebuildChild(identification, null);
    }

    public synchronized InfoObject rebuildChild(IdentificationDefinition identification,
                                                InfoStatusOpenDefinition statusOpen) {
        if (ObjectUtil.isAnyNull(identification)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<GetOrRebuildChildFunction> funcs = this.processorRegister.getGetOrRebuildChilds();

        InfoEntity childInfo = null;

        for (Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoStatusDefinition, IdentificationDefinition,
                InfoStatusOpenDefinition> pair : funcs) {
            childInfo = pair.apply(childInfo, info, type, this.status, identification, statusOpen);
        }

        if (ObjectUtil.isAnyNull(childInfo)) {
            throw new StatusUnexpectedException();
        }

        InfoCacheObject infoCache = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                InfoCacheObject.class);

        InfoObject childCachedInfo = infoCache.getIfExisted(SpaceType.ALL, childInfo.getID());
        if (ObjectUtil.isAnyNull(childCachedInfo)) {
            childCachedInfo = this.factory.buildInfoObject(childInfo, statusOpen, this);

            childCachedInfo.cache(SpaceType.USER);
        }
        return childCachedInfo;
    }

    public synchronized void deleteChild(IdentificationDefinition identification) {
        if (ObjectUtil.isAnyNull(identification)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getInfo();
        InfoObject childInfo = this.getChild(identification);

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<DeleteChildConsumer> funcs = this.processorRegister.getDeleteChilds();

        for (Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, IdentificationDefinition> pair : funcs) {
            pair.accept(info, type, this.status, identification);
        }

        childInfo.uncache(SpaceType.ALL);
    }

    public synchronized Set<InfoSummaryDefinition> queryChild(Predicate<InfoSummaryDefinition> wildcard) {
        if (ObjectUtil.isAnyNull(wildcard)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<QueryChildFunction> funcs = this.processorRegister.getQueryChilds();

        Set<InfoSummaryDefinition> infoSummaries = new HashSet<>();

        for (Function5<Set<InfoSummaryDefinition>, Set<InfoSummaryDefinition>, InfoEntity, TypeObject,
                InfoStatusDefinition, Predicate<InfoSummaryDefinition>> pair : funcs) {
            infoSummaries = pair.apply(infoSummaries, info, type, this.status, wildcard);
        }

        return infoSummaries;
    }

    public synchronized void renameChild(IdentificationDefinition oldIdentification,
                                         IdentificationDefinition newIdentification) {
        if (ObjectUtil.isAnyNull(oldIdentification, newIdentification)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<RenameChildConsumer> funcs = this.processorRegister.getRenameChilds();

        for (Consumer5<InfoEntity, TypeObject, InfoStatusDefinition, IdentificationDefinition,
                IdentificationDefinition> pair : funcs) {
            pair.accept(info, type, this.status, oldIdentification, newIdentification);
        }
    }

    public synchronized Map<String, String> readProperties() {
        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<ReadPropertyFunction> funcs = this.processorRegister.getReadProperties();

        Map<String, String> properties = new HashMap<>();

        for (Function4<Map<String, String>, Map<String, String>, InfoEntity, TypeObject, InfoStatusDefinition> pair :
                funcs) {
            properties = pair.apply(properties, info, type, this.status);
        }

        return Collections.unmodifiableMap(properties);
    }

    public synchronized void writeProperties(Map<String, String> properties) {
        if (ObjectUtil.isAnyNull(properties)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<WritePropertyConsumer> funcs = this.processorRegister.getWriteProperties();

        for (Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, Map<String, String>> pair : funcs) {
            pair.accept(info, type, this.status, properties);
        }
    }

    public synchronized AInfoContentObject getContent() {
        InfoEntity info = this.getInfo();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        AInfoContentObject content = type.getTypeInitializer().getContentProcedure(info, () -> {
            List<ReadContentFunction> funcs = this.processorRegister.getReadContents();

            byte[] contentSource = null;

            for (Function4<byte[], byte[], InfoEntity, TypeObject, InfoStatusDefinition> pair : funcs) {
                contentSource = pair.apply(contentSource, info, type, status);
            }

            return contentSource;
        }, (byte[] contentSource) -> {
            List<WriteContentConsumer> funcs = this.processorRegister.getWriteContents();

            for (Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, byte[]> pair : funcs) {
                pair.accept(info, type, status, contentSource);
            }
        }, this.status.getOpen());

        return content;
    }
}
