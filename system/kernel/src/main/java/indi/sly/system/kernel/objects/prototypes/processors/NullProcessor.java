package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.functions.*;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import indi.sly.system.kernel.objects.prototypes.InfoObjectStatusDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObjectStatusOpenDefinition;
import indi.sly.system.kernel.objects.types.prototypes.TypeObject;
import indi.sly.system.kernel.objects.prototypes.DumpDefinition;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

//@Named
//@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NullProcessor extends ACoreObject implements IInfoObjectProcessor {
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

    private final Function4<DumpDefinition, DumpDefinition, InfoEntity, TypeObject, InfoObjectStatusDefinition> dump;
    private final Function6<UUID, UUID, InfoEntity, TypeObject, InfoObjectStatusDefinition, Long, Object[]> open;
    private final Consumer3<InfoEntity, TypeObject, InfoObjectStatusDefinition> close;
    private final Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoObjectStatusDefinition, UUID, Identification> createChildAndOpen;
    private final Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoObjectStatusDefinition, Identification, InfoObjectStatusOpenDefinition> getOrRebuildChild;
    private final Consumer4<InfoEntity, TypeObject, InfoObjectStatusDefinition, Identification> deleteChild;
    private final Function5<Set<InfoSummaryDefinition>, Set<InfoSummaryDefinition>, InfoEntity, TypeObject, InfoObjectStatusDefinition, Predicate<InfoSummaryDefinition>> queryChild;
    private final Consumer5<InfoEntity, TypeObject, InfoObjectStatusDefinition, Identification, Identification> renameChild;
    private final Function4<Map<String, String>, Map<String, String>, InfoEntity, TypeObject, InfoObjectStatusDefinition> readProperties;
    private final Consumer4<InfoEntity, TypeObject, InfoObjectStatusDefinition, Map<String, String>> writeProperties;
    private final Function4<byte[], byte[], InfoEntity, TypeObject, InfoObjectStatusDefinition> readContent;
    private final Consumer4<InfoEntity, TypeObject, InfoObjectStatusDefinition, byte[]> writeContent;

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
