package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.*;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoObject extends AObject {
    protected InfoFactory factory;
    protected InfoProcessorMediator processorMediator;

    protected UUID id;
    protected InfoStatusDefinition status;

    public UUID getID() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.id;
    }

    public UUID getType() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.getSelf().getType();
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

        return CollectionUtil.unmodifiable(this.status.getIdentifications());
    }

    private synchronized InfoEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.id, this.status);
    }

    private synchronized UUID getIndex() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();
        ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(this.id);

        return processInfoEntry.getIndex();
    }

    public synchronized InfoObject getParent() {
        this.getSelf();

        if (this.status.getIdentifications().isEmpty()) {
            return null;
        } else {
            return this.processorMediator.getParent().apply(this.status);
        }
    }

    public synchronized Map<Long, Long> getDate() {
        InfoEntity info = this.getSelf();

        Map<Long, Long> date = ObjectUtil.transferFromByteArray(info.getDate());

        return CollectionUtil.unmodifiable(date);
    }

    public synchronized SecurityDescriptorObject getSecurityDescriptor() {
        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        InfoProcessorSecurityDescriptorFunction resolver = this.processorMediator.getSecurityDescriptor();

        if (ObjectUtil.isAnyNull(resolver)) {
            throw new StatusDisabilityException();
        }

        return resolver.apply(info, type, this.status);
    }

    public synchronized DumpObject dump() {
        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorDumpFunction> resolvers = this.processorMediator.getDumps();

        DumpDefinition dump = new DumpDefinition();

        for (InfoProcessorDumpFunction resolver : resolvers) {
            dump = resolver.apply(dump, info, type, this.status);
        }

        return this.factory.buildDump(this, dump);
    }

    public synchronized UUID open(long openAttribute, Object... arguments) {
        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());
        AInfoTypeInitializer infoTypeInitializer = type.getInitializer();

        List<InfoProcessorOpenFunction> resolvers = this.processorMediator.getOpens();

        UUID index = UUIDUtil.getEmpty();

        try {
            infoTypeInitializer.lockProcedure(info, LockType.WRITE);

            for (InfoProcessorOpenFunction resolver : resolvers) {
                index = resolver.apply(index, info, type, this.status, openAttribute, arguments);
                if (ObjectUtil.isAnyNull(index)) {
                    throw new StatusUnexpectedException();
                }
            }
        } finally {
            infoTypeInitializer.unlockProcedure(info, LockType.WRITE);
        }

        return index;
    }

    public synchronized void close() {
        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());
        AInfoTypeInitializer infoTypeInitializer = type.getInitializer();

        List<InfoProcessorCloseFunction> resolvers = this.processorMediator.getCloses();

        try {
            infoTypeInitializer.lockProcedure(info, LockType.WRITE);

            for (InfoProcessorCloseFunction resolver : resolvers) {
                info = resolver.apply(info, type, this.status);
            }
        } finally {
            if (ObjectUtil.allNotNull(info)) {
                infoTypeInitializer.unlockProcedure(info, LockType.WRITE);
            }
        }
    }

    public synchronized long getOpenAttribute() {
        if (ValueUtil.isAnyNullOrEmpty(this.id)) {
            throw new ConditionContextException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();

        if (!processInfoTable.containByID(this.id)) {
            return InfoOpenAttributeType.CLOSE;
        } else {
            ProcessInfoEntryObject processInfoTableEntry = processInfoTable.getByID(this.id);

            return processInfoTableEntry.getOpen().getAttribute();
        }
    }

    public synchronized InfoObject createChildAndOpen(UUID type, IdentificationDefinition identification, long openAttribute, Object... arguments) {
        if (ObjectUtil.isAnyNull(identification)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isNull(arguments)) {
            arguments = new Object[0];
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject typeObject = typeManager.get(this.getType());

        List<InfoProcessorCreateChildFunction> resolvers = this.processorMediator.getCreateChilds();

        InfoEntity childInfo = null;

        for (InfoProcessorCreateChildFunction resolver : resolvers) {
            childInfo = resolver.apply(childInfo, info, typeObject, this.status, type, identification);
        }

        if (ObjectUtil.isAnyNull(info)) {
            throw new StatusUnexpectedException();
        }

        Object[] additionalArguments = arguments;
        return this.factory.buildInfo(childInfo, this, additionalChildInfo -> {
            additionalChildInfo.open(openAttribute, additionalArguments);
            return additionalChildInfo;
        });
    }

    public synchronized InfoObject getChild(IdentificationDefinition identification) {
        if (ObjectUtil.isAnyNull(identification)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorGetOrRebuildChildFunction> resolvers = this.processorMediator.getGetOrRebuildChilds();

        InfoEntity childInfo = null;

        for (InfoProcessorGetOrRebuildChildFunction resolver : resolvers) {
            childInfo = resolver.apply(childInfo, info, type, this.status, identification);
        }

        if (ObjectUtil.isAnyNull(childInfo)) {
            throw new StatusUnexpectedException();
        }

        return this.factory.buildInfo(childInfo, this);
    }

    public synchronized void deleteChild(IdentificationDefinition identification) {
        if (ObjectUtil.isAnyNull(identification)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorDeleteChildConsumer> resolvers = this.processorMediator.getDeleteChilds();

        for (InfoProcessorDeleteChildConsumer resolver : resolvers) {
            resolver.accept(info, type, this.status, identification);
        }
    }

    public synchronized Set<InfoSummaryDefinition> queryChild(InfoWildcardDefinition wildcard) {
        if (ObjectUtil.isAnyNull(wildcard)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorQueryChildFunction> resolvers = this.processorMediator.getQueryChilds();

        Set<InfoSummaryDefinition> infoSummaries = new HashSet<>();

        for (InfoProcessorQueryChildFunction resolver : resolvers) {
            infoSummaries = resolver.apply(infoSummaries, info, type, this.status, wildcard);
        }

        return infoSummaries;
    }

    public synchronized void renameChild(IdentificationDefinition oldIdentification, IdentificationDefinition newIdentification) {
        if (ObjectUtil.isAnyNull(oldIdentification, newIdentification)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorRenameChildConsumer> resolvers = this.processorMediator.getRenameChilds();

        for (InfoProcessorRenameChildConsumer resolver : resolvers) {
            resolver.accept(info, type, this.status, oldIdentification, newIdentification);
        }
    }

    public synchronized Map<String, String> readProperties() {
        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorReadPropertyFunction> resolvers = this.processorMediator.getReadProperties();

        Map<String, String> properties = new HashMap<>();

        for (InfoProcessorReadPropertyFunction resolver : resolvers) {
            properties = resolver.apply(properties, info, type, this.status);
        }

        return CollectionUtil.unmodifiable(properties);
    }

    public synchronized void writeProperties(Map<String, String> properties) {
        if (ObjectUtil.isAnyNull(properties)) {
            throw new ConditionParametersException();
        }

        InfoEntity info = this.getSelf();

        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        List<InfoProcessorWritePropertyConsumer> resolvers = this.processorMediator.getWriteProperties();

        for (InfoProcessorWritePropertyConsumer resolver : resolvers) {
            resolver.accept(info, type, this.status, properties);
        }
    }

    public synchronized AInfoContentObject getContent() {
        TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.getType());

        AInfoContentObject content = type.getInitializer().getContentProcedure(this::getSelf, () -> {
            InfoEntity info = this.getSelf();

            List<InfoProcessorReadContentFunction> resolvers = this.processorMediator.getReadContents();

            byte[] source = null;

            for (InfoProcessorReadContentFunction resolver : resolvers) {
                source = resolver.apply(source, info, type, status);
            }

            return source;
        }, (byte[] source) -> {
            InfoEntity info = this.getSelf();

            List<InfoProcessorWriteContentConsumer> resolvers = this.processorMediator.getWriteContents();

            for (InfoProcessorWriteContentConsumer resolver : resolvers) {
                resolver.accept(info, type, status, source);
            }
        }, () -> {
            InfoEntity info = this.getSelf();

            List<InfoProcessorExecuteContentConsumer> resolvers = this.processorMediator.getExecuteContents();

            for (InfoProcessorExecuteContentConsumer resolver : resolvers) {
                resolver.accept(info, type, status);
            }
        });
        content.setParent(this);

        return content;
    }
}
