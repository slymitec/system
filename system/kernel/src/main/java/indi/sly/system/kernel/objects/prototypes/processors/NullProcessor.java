package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.functions.*;
import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.DumpDefinition;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

//@Named
//@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NullProcessor extends ACorePrototype implements IInfoObjectProcessor {
    public NullProcessor() {
        this.dump = (dump, info, type, status) -> dump;

        this.open = (handle, info, type, status, openAttribute, arguments) -> handle;

        this.close = (info, type, status) -> {
        };

        this.createChildAndOpen = (childInfo, info, type, status, childType, identification) -> childInfo;

        this.getOrRebuildChild = (childInfo, info, type, status, identification, statusOpen) -> childInfo;

        this.deleteChild = (info, type, status, identification) -> {
        };

        this.queryChild = (summaryDefinitions, info, type, status, queryChild) -> summaryDefinitions;

        this.renameChild = (info, type, status, oldIdentification, newIdentification) -> {
        };

        this.readProperties = (properties, info, type, status) -> properties;

        this.writeProperties = (info, type, status, properties) -> {
        };

        this.readContent = (content, info, type, status) -> content;

        this.writeContent = (info, type, status, content) -> {
        };
    }

    private final Function4<DumpDefinition, DumpDefinition, InfoEntity, TypeObject, InfoStatusDefinition> dump;
    private final Function6<UUID, UUID, InfoEntity, TypeObject, InfoStatusDefinition, Long, Object[]> open;
    private final Consumer3<InfoEntity, TypeObject, InfoStatusDefinition> close;
    private final Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoStatusDefinition, UUID, Identification> createChildAndOpen;
    private final Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoStatusDefinition, Identification, InfoStatusOpenDefinition> getOrRebuildChild;
    private final Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, Identification> deleteChild;
    private final Function5<Set<InfoSummaryDefinition>, Set<InfoSummaryDefinition>, InfoEntity, TypeObject, InfoStatusDefinition, Predicate<InfoSummaryDefinition>> queryChild;
    private final Consumer5<InfoEntity, TypeObject, InfoStatusDefinition, Identification, Identification> renameChild;
    private final Function4<Map<String, String>, Map<String, String>, InfoEntity, TypeObject, InfoStatusDefinition> readProperties;
    private final Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, Map<String, String>> writeProperties;
    private final Function4<byte[], byte[], InfoEntity, TypeObject, InfoStatusDefinition> readContent;
    private final Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, byte[]> writeContent;

    @Override
    public void process(InfoEntity info, InfoObjectProcessorRegister processorRegister) {
        processorRegister.getDumps().add(this.dump);
        processorRegister.getOpens().add(this.open);
        processorRegister.getCloses().add(this.close);
        processorRegister.getCreateChildAndOpens().add(this.createChildAndOpen);
        processorRegister.getGetOrRebuildChilds().add(this.getOrRebuildChild);
        processorRegister.getDeleteChilds().add(this.deleteChild);
        processorRegister.getQueryChilds().add(this.queryChild);
        processorRegister.getRenameChilds().add(this.renameChild);
        processorRegister.getReadProperties().add(this.readProperties);
        processorRegister.getWriteProperties().add(this.writeProperties);
        processorRegister.getReadContents().add(this.readContent);
        processorRegister.getWriteContents().add(this.writeContent);
    }
}
