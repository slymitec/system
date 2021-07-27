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
import indi.sly.system.kernel.objects.values.*;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
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
    protected InfoProcessorMediator processorMediator;

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

        return this.getSelf().getType();
    }

    public long getOccupied() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.getSelf().getOccupied();
    }

    public long getOpened() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.getSelf().getOpened();
    }

    public String getName() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.getSelf().getName();
    }

    public List<IdentificationDefinition> getIdentifications() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return Collections.unmodifiableList(this.status.getIdentifications());
    }

    private synchronized void occupy() {
        InfoEntity info = this.getSelf();
        info.setOccupied(info.getOccupied() + 1);

        InfoObject parent = this.getParent();
        if (ObjectUtil.allNotNull(parent)) {
            parent.occupy();
        }
    }

    private synchronized void free() {
        InfoEntity info = this.getSelf();
        info.setOccupied(info.getOccupied() - 1);

        InfoObject parent = this.getParent();
        if (ObjectUtil.allNotNull(parent)) {
            parent.free();
        }
    }

    private synchronized void cache(long space) {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        InfoCacheObject kernelCache = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                InfoCacheObject.class);

        if (LogicalUtil.isAnyExist(space, SpaceType.KERNEL)) {
            if (!currentProcessToken.isPrivileges(PrivilegeType.MEMORY_CACHE_MODIFY_KERNEL_SPACE_CACHE)) {
                throw new ConditionPermissionsException();
            }

            kernelCache.add(SpaceType.KERNEL, this);
        }
        if (LogicalUtil.isAnyExist(space, SpaceType.USER)) {
            kernelCache.add(SpaceType.USER, this);
        }
    }

    private synchronized void uncache(long space) {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        InfoCacheObject kernelCache = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                InfoCacheObject.class);

        if (LogicalUtil.isAnyExist(space, SpaceType.KERNEL)) {
            if (!currentProcessToken.isPrivileges(PrivilegeType.MEMORY_CACHE_MODIFY_KERNEL_SPACE_CACHE)) {
                throw new ConditionPermissionsException();
            }

            kernelCache.delete(SpaceType.KERNEL, this.id);
        }
        if (LogicalUtil.isAnyExist(space, SpaceType.USER)) {
            kernelCache.delete(SpaceType.USER, this.id);
        }
    }

    private synchronized InfoEntity getSelf() {
        return this.processorMediator.getSelf().apply(this.poolID, this.id, this.status);
    }

    private synchronized UUID getHandle() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessHandleTableObject processHandleTable = process.getHandleTable();
        ProcessHandleEntryObject processHandleTableEntry = processHandleTable.getEntry(this.id, this.status);

        return processHandleTableEntry.getHandle();
    }

    public synchronized InfoObject getParent() {
        InfoEntity info = this.getSelf();

        if (ValueUtil.isAnyNullOrEmpty(this.status.getParentID())) {
            return null;
        } else {
            return this.processorMediator.getParent().apply(this.status.getParentID());
        }
    }

    public synchronized Map<Long, Long> getDate() {
        InfoEntity info = this.getSelf();

        Map<Long, Long> date = ObjectUtil.transferFromByteArray(info.getDate());

        return Collections.unmodifiableMap(date);
    }

    public synchronized SecurityDescriptorObject getSecurityDescriptor() {
        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        SecurityDescriptorFunction resolver = this.processorMediator.getSecurityDescriptor();

        if (ObjectUtil.isAnyNull(resolver)) {
            throw new StatusNotSupportedException();
        }

        return resolver.apply(info, type, this.status);
    }

    public synchronized DumpObject dump() {
        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<DumpFunction> resolvers = this.processorMediator.getDumps();

        DumpDefinition dump = new DumpDefinition();

        for (DumpFunction pair : resolvers) {
            dump = pair.apply(dump, info, type, this.status);
        }

        return this.factory.buildDump(dump);
    }

    public synchronized UUID open(long openAttribute, Object... arguments) {
        if (this.isOpened()) {
            throw new StatusAlreadyFinishedException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<OpenFunction> resolvers = this.processorMediator.getOpens();

        UUID handle = UUIDUtil.getEmpty();

        for (OpenFunction resolver : resolvers) {
            handle = resolver.apply(handle, info, type, this.status, openAttribute, arguments);
            if (ObjectUtil.isAnyNull(handle)) {
                throw new StatusUnexpectedException();
            }
        }

        InfoObject parentInfo = this.getParent();
        if (ObjectUtil.allNotNull(parentInfo)) {
            parentInfo.occupy();
        }

        return handle;
    }

    public synchronized void close() {
        InfoEntity info = this.getSelf();

        if (!this.isOpened()) {
            throw new StatusAlreadyFinishedException();
        }

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<CloseConsumer> resolvers = this.processorMediator.getCloses();

        for (CloseConsumer resolver : resolvers) {
            resolver.accept(info, type, this.status);
        }

        InfoObject parentInfo = this.getParent();
        if (ObjectUtil.allNotNull(parentInfo)) {
            parentInfo.free();
        }
    }

    public synchronized boolean isOpened() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessHandleTableObject processHandleTable = process.getHandleTable();
        ProcessHandleEntryObject processHandleTableEntry = processHandleTable.getEntry(this.id, this.status);

        return processHandleTableEntry.isExist();
    }

    public synchronized long getOpenAttribute() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessHandleTableObject processHandleTable = process.getHandleTable();
        ProcessHandleEntryObject processHandleTableEntry = processHandleTable.getEntry(this.id, this.status);

        if (!processHandleTableEntry.isExist()) {
            return InfoOpenAttributeType.CLOSE;
        }

        return processHandleTableEntry.getOpen().getAttribute();
    }

    public synchronized InfoObject createChildAndOpen(UUID type, IdentificationDefinition identification,
                                                      long openAttribute, Object... arguments) {
        if (!ValueUtil.isAnyNullOrEmpty(this.getHandle()) || ObjectUtil.isAnyNull(identification)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(arguments)) {
            arguments = new Object[0];
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject typeObject = typeManager.get(this.getType());

        List<CreateChildAndOpenFunction> resolvers = this.processorMediator.getCreateChildAndOpens();

        InfoEntity childInfo = null;

        for (CreateChildAndOpenFunction resolver : resolvers) {
            childInfo = resolver.apply(childInfo, info, typeObject, this.status, type, identification);
        }

        if (ObjectUtil.isAnyNull(info)) {
            throw new StatusUnexpectedException();
        }

        InfoObject childInfoObject = this.factory.buildInfo(childInfo, this);

        childInfoObject.open(openAttribute, arguments);

        return childInfoObject;
    }

    public synchronized InfoObject getChild(IdentificationDefinition identification) {
        return this.rebuildChild(identification, null);
    }

    public synchronized InfoObject rebuildChild(IdentificationDefinition identification, InfoOpenDefinition open) {
        if (ObjectUtil.isAnyNull(identification)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<GetOrRebuildChildFunction> resolvers = this.processorMediator.getGetOrRebuildChilds();

        InfoEntity childInfo = null;

        for (GetOrRebuildChildFunction resolver : resolvers) {
            childInfo = resolver.apply(childInfo, info, type, this.status, identification, open);
        }

        if (ObjectUtil.isAnyNull(childInfo)) {
            throw new StatusUnexpectedException();
        }

        InfoCacheObject infoCache = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                InfoCacheObject.class);

        InfoObject childCachedInfo = infoCache.getIfExisted(SpaceType.ALL, childInfo.getID());
        if (ObjectUtil.isAnyNull(childCachedInfo)) {
            childCachedInfo = this.factory.buildInfo(childInfo, this);

            childCachedInfo.cache(SpaceType.USER);
        }
        return childCachedInfo;
    }

    public synchronized void deleteChild(IdentificationDefinition identification) {
        if (ObjectUtil.isAnyNull(identification)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();
        InfoObject childInfo = this.getChild(identification);

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<DeleteChildConsumer> resolvers = this.processorMediator.getDeleteChilds();

        for (DeleteChildConsumer resolver : resolvers) {
            resolver.accept(info, type, this.status, identification);
        }

        childInfo.uncache(SpaceType.ALL);
    }

    public synchronized Set<InfoSummaryDefinition> queryChild(Predicate<InfoSummaryDefinition> wildcard) {
        if (ObjectUtil.isAnyNull(wildcard)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<QueryChildFunction> resolvers = this.processorMediator.getQueryChilds();

        Set<InfoSummaryDefinition> infoSummaries = new HashSet<>();

        for (QueryChildFunction resolver : resolvers) {
            infoSummaries = resolver.apply(infoSummaries, info, type, this.status, wildcard);
        }

        return infoSummaries;
    }

    public synchronized void renameChild(IdentificationDefinition oldIdentification,
                                         IdentificationDefinition newIdentification) {
        if (ObjectUtil.isAnyNull(oldIdentification, newIdentification)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<RenameChildConsumer> resolvers = this.processorMediator.getRenameChilds();

        for (RenameChildConsumer resolver : resolvers) {
            resolver.accept(info, type, this.status, oldIdentification, newIdentification);
        }
    }

    public synchronized Map<String, String> readProperties() {
        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<ReadPropertyFunction> resolvers = this.processorMediator.getReadProperties();

        Map<String, String> properties = new HashMap<>();

        for (ReadPropertyFunction resolver : resolvers) {
            properties = resolver.apply(properties, info, type, this.status);
        }

        return Collections.unmodifiableMap(properties);
    }

    public synchronized void writeProperties(Map<String, String> properties) {
        if (ObjectUtil.isAnyNull(properties)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<WritePropertyConsumer> resolvers = this.processorMediator.getWriteProperties();

        for (WritePropertyConsumer resolver : resolvers) {
            resolver.accept(info, type, this.status, properties);
        }
    }

    public synchronized AInfoContentObject getContent() {
        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessHandleTableObject processHandleTable = process.getHandleTable();
        ProcessHandleEntryObject processHandleTableEntry = processHandleTable.getEntry(this.id, this.status);

        AInfoContentObject content = type.getTypeInitializer().getContentProcedure(info, () -> {
            List<ReadContentFunction> resolvers = this.processorMediator.getReadContents();

            byte[] contentSource = null;

            for (ReadContentFunction resolver : resolvers) {
                contentSource = resolver.apply(contentSource, info, type, status);
            }

            return contentSource;
        }, (byte[] contentSource) -> {
            List<WriteContentConsumer> resolvers = this.processorMediator.getWriteContents();

            for (WriteContentConsumer resolver : resolvers) {
                resolver.accept(info, type, status, contentSource);
            }
        }, () -> {
            List<ExecuteContentConsumer> resolvers = this.processorMediator.getExecuteContents();

            for (ExecuteContentConsumer resolver : resolvers) {
                resolver.accept(info, type, status);
            }
        }, processHandleTableEntry.getOpen());

        content.setInfo(this);

        return content;
    }
}
