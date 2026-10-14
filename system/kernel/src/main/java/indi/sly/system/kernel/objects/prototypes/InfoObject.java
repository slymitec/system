package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.mediators.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.*;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.values.SecurityDescriptorCacheEntity;
import jakarta.inject.Named;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoObject extends ACacheableObject<InfoCacheEntity> {
    @SuppressWarnings("NullAway.Init")
    protected InfoFactory factory;
    @SuppressWarnings("NullAway.Init")
    protected InfoProcessorMediator processorMediator;

    private InfoEntity getSelf(long lock) {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfoId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache, lock);
    }

    public void initSelf(long lock) {
        this.getSelf(lock);
    }

    public UUID getId() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfoId())) {
            throw new ConditionContextException();
        }

        return this.cache.getInfoId();
    }

    public UUID getType() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfoId())) {
            throw new ConditionContextException();
        }

        return this.getSelf(LockType.READ).getType();
    }

    public long getOpened() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfoId())) {
            throw new ConditionContextException();
        }

        return this.getSelf(LockType.READ).getOpened();
    }

    public @Nullable String getName() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfoId())) {
            throw new ConditionContextException();
        }

        return this.getSelf(LockType.READ).getName();
    }

    public PathRecord getPath() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfoId())) {
            throw new ConditionContextException();
        }

        return this.cache.getPath();
    }

    public UUID getIndex() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfoId())) {
            throw new ConditionContextException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();
        ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(this.cache.getInfoId());

        return processInfoEntry.getIndex();
    }

    public @Nullable InfoObject getParent() {
        if (this.cache.getPath().identifiers().isEmpty()) {
            return null;
        } else {
            return this.processorMediator.getParent().apply(this.cache);
        }
    }

    public Map<Long, Long> getDate() {
        InfoEntity info = this.getSelf(LockType.READ);

        Map<Long, Long> date = info.getDate();

        return CollectionUtil.unmodifiable(date);
    }

    public SecurityDescriptorObject getSecurityDescriptor() {
        InfoEntity info = this.getSelf(LockType.READ);

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        InfoProcessorSecurityDescriptorFunction resolver = this.processorMediator.getSecurityDescriptor();

        SecurityDescriptorCacheEntity securityDescriptorCache = resolver.apply(info, type, this.cache);

        return this.factory.buildSecurityDescriptor(processorMediator, this, securityDescriptorCache);
    }

    public DumpObject dump() {
        InfoEntity info = this.getSelf(LockType.READ);

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorDumpFunction> resolvers = this.processorMediator.getDumps();

        DumpCacheEntity dumpCache = new DumpCacheEntity();

        for (InfoProcessorDumpFunction resolver : resolvers) {
            dumpCache = resolver.apply(dumpCache, info, type, this.cache);
        }

        return this.factory.buildDump(dumpCache);
    }

    public UUID open(long openAttribute, Object... arguments) {
        InfoEntity info = this.getSelf(LockType.WRITE);

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorOpenFunction> resolvers = this.processorMediator.getOpens();

        UUID index = UUIDUtil.getEmpty();

        for (InfoProcessorOpenFunction resolver : resolvers) {
            index = resolver.apply(index, info, type, this.cache, openAttribute, arguments);
            if (ObjectUtil.isNull(index)) {
                throw new StatusUnexpectedException();
            }
        }

        return index;
    }

    public void close() {
        InfoEntity info = this.getSelf(LockType.WRITE);

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorCloseFunction> resolvers = this.processorMediator.getCloses();

        for (InfoProcessorCloseFunction resolver : resolvers) {
            info = resolver.apply(info, type, this.cache);
        }
    }

    public long getOpenAttribute() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfoId())) {
            throw new ConditionContextException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();

        if (!processInfoTable.containById(this.cache.getInfoId())) {
            return InfoOpenAttributeType.CLOSE;
        } else {
            ProcessInfoEntryObject processInfoTableEntry = processInfoTable.getById(this.cache.getInfoId());

            return processInfoTableEntry.getOpen().attribute();
        }
    }

    public InfoObject createChild(UUID childType, IdentifierRecord identifier) {
        if (ObjectUtil.isNull(identifier)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf(LockType.WRITE);

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorCreateChildFunction> resolvers = this.processorMediator.getCreateChildren();

        InfoEntity childInfo = null;

        for (InfoProcessorCreateChildFunction resolver : resolvers) {
            childInfo = resolver.apply(childInfo, info, type, this.cache, childType, identifier);
        }

        if (ObjectUtil.isNull(childInfo)) {
            throw new StatusUnexpectedException();
        }

        return this.factory.buildInfo(childInfo, this.cache);
    }

    public InfoObject getChild(IdentifierRecord identifier) {
        if (ObjectUtil.isNull(identifier)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf(LockType.READ);

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorGetChildFunction> resolvers = this.processorMediator.getGetChildren();

        InfoEntity childInfo = null;

        for (InfoProcessorGetChildFunction resolver : resolvers) {
            childInfo = resolver.apply(childInfo, info, type, this.cache, identifier);
        }

        if (ObjectUtil.isNull(childInfo)) {
            throw new StatusUnexpectedException();
        }

        return this.factory.buildInfo(childInfo, this.cache);
    }

    public void deleteChild(IdentifierRecord identifier) {
        if (ObjectUtil.isNull(identifier)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf(LockType.WRITE);

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorDeleteChildConsumer> resolvers = this.processorMediator.getDeleteChildren();

        for (InfoProcessorDeleteChildConsumer resolver : resolvers) {
            resolver.accept(info, type, this.cache, identifier);
        }
    }

    public Set<InfoSummaryRecord> queryChild(InfoWildcardRecord wildcard) {
        if (ObjectUtil.isNull(wildcard)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf(LockType.READ);

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorQueryChildFunction> resolvers = this.processorMediator.getQueryChildren();

        Set<InfoSummaryRecord> infoSummaries = new HashSet<>();

        for (InfoProcessorQueryChildFunction resolver : resolvers) {
            infoSummaries = resolver.apply(infoSummaries, info, type, this.cache, wildcard);
        }

        return infoSummaries;
    }

    public void renameChild(IdentifierRecord oldIdentifier, IdentifierRecord newIdentifier) {
        if (ObjectUtil.isAnyNull(oldIdentifier, newIdentifier)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf(LockType.WRITE);

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorRenameChildConsumer> resolvers = this.processorMediator.getRenameChildren();

        for (InfoProcessorRenameChildConsumer resolver : resolvers) {
            resolver.accept(info, type, this.cache, oldIdentifier, newIdentifier);
        }
    }

    public Map<String, String> readProperties() {
        InfoEntity info = this.getSelf(LockType.READ);

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorReadPropertyFunction> resolvers = this.processorMediator.getReadProperties();

        Map<String, String> properties = new HashMap<>();

        for (InfoProcessorReadPropertyFunction resolver : resolvers) {
            properties = resolver.apply(properties, info, type, this.cache);
        }

        return CollectionUtil.unmodifiable(properties);
    }

    public void writeProperties(Map<String, String> properties) {
        if (ObjectUtil.isNull(properties)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf(LockType.WRITE);

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorWritePropertyConsumer> resolvers = this.processorMediator.getWriteProperties();

        for (InfoProcessorWritePropertyConsumer resolver : resolvers) {
            resolver.accept(info, type, this.cache, properties);
        }
    }

    public AInfoContentObject getContent() {
        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();

        InfoOpenRecord infoOpen = null;
        if (processInfoTable.containById(this.getId())) {
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(this.getId());
            infoOpen = processInfoEntry.getOpen();
        }

        Class<? extends AInfoContentObject> contentType = type.getInitializer().getContentTypeProcedure(this.getSelf(LockType.READ), infoOpen);

        return this.factory.buildInfoContent(this.processorMediator, this, contentType);
    }
}
