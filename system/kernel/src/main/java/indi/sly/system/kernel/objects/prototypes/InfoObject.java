package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.mediators.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.*;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.SecurityDescriptorCacheEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoObject extends ACacheableObject<InfoCacheEntity> {
    protected InfoFactory factory;
    protected InfoProcessorMediator processorMediator;

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

        return this.getSelf().getType();
    }

    public long getOpened() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfoId())) {
            throw new ConditionContextException();
        }

        return this.getSelf().getOpened();
    }

    public String getName() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfoId())) {
            throw new ConditionContextException();
        }

        return this.getSelf().getName();
    }

    public PathDefinition getPath() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfoId())) {
            throw new ConditionContextException();
        }

        return this.cache.getPath();
    }

    private InfoEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfoId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache);
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

    public InfoObject getParent() {
        if (this.cache.getPath().get().isEmpty()) {
            return null;
        } else {
            return this.processorMediator.getParent().apply(this.cache);
        }
    }

    public Map<Long, Long> getDate() {
        InfoEntity info = this.getSelf();

        Map<Long, Long> date = info.getDate();

        return CollectionUtil.unmodifiable(date);
    }

    public SecurityDescriptorObject getSecurityDescriptor() {
        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        InfoProcessorSecurityDescriptorFunction resolver = this.processorMediator.getSecurityDescriptor();

        SecurityDescriptorCacheEntity securityDescriptorCache = resolver.apply(info, type, this.cache);

        return this.factory.buildSecurityDescriptor(processorMediator, this, securityDescriptorCache);
    }

    public DumpObject dump() {
        InfoEntity info = this.getSelf();

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
        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());
        AInfoTypeInitializer infoTypeInitializer = type.getInitializer();

        List<InfoProcessorOpenFunction> resolvers = this.processorMediator.getOpens();

        UUID index = UUIDUtil.getEmpty();

        try {
            infoTypeInitializer.lockProcedure(info, LockType.WRITE);

            for (InfoProcessorOpenFunction resolver : resolvers) {
                index = resolver.apply(index, info, type, this.cache, openAttribute, arguments);
                if (ObjectUtil.isAnyNull(index)) {
                    throw new StatusUnexpectedException();
                }
            }
        } finally {
            infoTypeInitializer.unlockProcedure(info, LockType.WRITE);
        }

        return index;
    }

    public void close() {
        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());
        AInfoTypeInitializer infoTypeInitializer = type.getInitializer();

        List<InfoProcessorCloseFunction> resolvers = this.processorMediator.getCloses();

        try {
            infoTypeInitializer.lockProcedure(info, LockType.WRITE);

            for (InfoProcessorCloseFunction resolver : resolvers) {
                info = resolver.apply(info, type, this.cache);
            }
        } finally {
            if (ObjectUtil.allNotNull(info)) {
                infoTypeInitializer.unlockProcedure(info, LockType.WRITE);
            }
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

            return processInfoTableEntry.getOpen().getAttribute();
        }
    }

    public InfoObject createChild(UUID childType, IdentifierDefinition identifier) {
        if (ObjectUtil.isAnyNull(identifier)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorCreateChildFunction> resolvers = this.processorMediator.getCreateChildren();

        InfoEntity childInfo = null;

        for (InfoProcessorCreateChildFunction resolver : resolvers) {
            childInfo = resolver.apply(childInfo, info, type, this.cache, childType, identifier);
        }

        if (ObjectUtil.isAnyNull(info)) {
            throw new StatusUnexpectedException();
        }

        return this.factory.buildInfo(childInfo, this.cache);
    }

    public InfoObject getChild(IdentifierDefinition identifier) {
        if (ObjectUtil.isAnyNull(identifier)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorGetChildFunction> resolvers = this.processorMediator.getGetChildren();

        InfoEntity childInfo = null;

        for (InfoProcessorGetChildFunction resolver : resolvers) {
            childInfo = resolver.apply(childInfo, info, type, this.cache, identifier);
        }

        if (ObjectUtil.isAnyNull(childInfo)) {
            throw new StatusUnexpectedException();
        }

        return this.factory.buildInfo(childInfo, this.cache);
    }

    public void deleteChild(IdentifierDefinition identifier) {
        if (ObjectUtil.isAnyNull(identifier)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorDeleteChildConsumer> resolvers = this.processorMediator.getDeleteChildren();

        for (InfoProcessorDeleteChildConsumer resolver : resolvers) {
            resolver.accept(info, type, this.cache, identifier);
        }
    }

    public Set<InfoSummaryDefinition> queryChild(InfoWildcardDefinition wildcard) {
        if (ObjectUtil.isAnyNull(wildcard)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorQueryChildFunction> resolvers = this.processorMediator.getQueryChildren();

        Set<InfoSummaryDefinition> infoSummaries = new HashSet<>();

        for (InfoProcessorQueryChildFunction resolver : resolvers) {
            infoSummaries = resolver.apply(infoSummaries, info, type, this.cache, wildcard);
        }

        return infoSummaries;
    }

    public void renameChild(IdentifierDefinition oldIdentifier, IdentifierDefinition newIdentifier) {
        if (ObjectUtil.isAnyNull(oldIdentifier, newIdentifier)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorRenameChildConsumer> resolvers = this.processorMediator.getRenameChildren();

        for (InfoProcessorRenameChildConsumer resolver : resolvers) {
            resolver.accept(info, type, this.cache, oldIdentifier, newIdentifier);
        }
    }

    public Map<String, String> readProperties() {
        InfoEntity info = this.getSelf();

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
        if (ObjectUtil.isAnyNull(properties)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

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

        InfoOpenDefinition infoOpen = null;
        if (processInfoTable.containById(this.getId())) {
            ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(this.getId());
            infoOpen = processInfoEntry.getOpen();
        }

        Class<? extends AInfoContentObject> contentType = type.getInitializer().getContentTypeProcedure(this.getSelf(), infoOpen);

        return this.factory.buildInfoContent(processorMediator, this, contentType);
    }
}
