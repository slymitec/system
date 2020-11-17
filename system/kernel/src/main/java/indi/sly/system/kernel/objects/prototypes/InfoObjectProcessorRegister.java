package indi.sly.system.kernel.objects.prototypes;

import java.util.*;
import java.util.function.Predicate;

import indi.sly.system.common.functions.*;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.types.prototypes.TypeObject;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;

public class InfoObjectProcessorRegister {
    public InfoObjectProcessorRegister() {
        this.dumps = new ArrayList<>();
        this.opens = new ArrayList<>();
        this.closes = new ArrayList<>();
        this.createChildAndOpens = new ArrayList<>();
        this.getOrRebuildChilds = new ArrayList<>();
        this.deleteChilds = new ArrayList<>();
        this.queryChilds = new ArrayList<>();
        this.renameChilds = new ArrayList<>();
        this.readProperties = new ArrayList<>();
        this.writeProperties = new ArrayList<>();
        this.readContents = new ArrayList<>();
        this.writeContents = new ArrayList<>();
    }

    private Function3<InfoEntity, UUID, UUID, InfoObjectStatusDefinition> info;
    private Function<InfoObject, UUID> parent;
    private Function3<SecurityDescriptorObject, InfoEntity, TypeObject, InfoObjectStatusDefinition> securityDescriptor;
    private final List<Function4<DumpDefinition, DumpDefinition, InfoEntity, TypeObject, InfoObjectStatusDefinition>> dumps;
    private final List<Function6<UUID, UUID, InfoEntity, TypeObject, InfoObjectStatusDefinition, Long, Object[]>> opens;
    private final List<Consumer3<InfoEntity, TypeObject, InfoObjectStatusDefinition>> closes;
    private final List<Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoObjectStatusDefinition, UUID, Identification>> createChildAndOpens;
    private final List<Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoObjectStatusDefinition, Identification, InfoObjectStatusOpenDefinition>> getOrRebuildChilds;
    private final List<Consumer4<InfoEntity, TypeObject, InfoObjectStatusDefinition, Identification>> deleteChilds;
    private final List<Function5<Set<InfoSummaryDefinition>, Set<InfoSummaryDefinition>, InfoEntity, TypeObject, InfoObjectStatusDefinition, Predicate<InfoSummaryDefinition>>> queryChilds;
    private final List<Consumer5<InfoEntity, TypeObject, InfoObjectStatusDefinition, Identification, Identification>> renameChilds;
    private final List<Function4<Map<String, String>, Map<String, String>, InfoEntity, TypeObject, InfoObjectStatusDefinition>> readProperties;
    private final List<Consumer4<InfoEntity, TypeObject, InfoObjectStatusDefinition, Map<String, String>>> writeProperties;
    private final List<Function4<byte[], byte[], InfoEntity, TypeObject, InfoObjectStatusDefinition>> readContents;
    private final List<Consumer4<InfoEntity, TypeObject, InfoObjectStatusDefinition, byte[]>> writeContents;

    public Function3<InfoEntity, UUID, UUID, InfoObjectStatusDefinition> getInfo() {
        return this.info;
    }

    public void setInfo(Function3<InfoEntity, UUID, UUID, InfoObjectStatusDefinition> info) {
        this.info = info;
    }

    public Function<InfoObject, UUID> getParent() {
        return this.parent;
    }

    public void setParent(Function<InfoObject, UUID> parent) {
        this.parent = parent;
    }

    public List<Function4<DumpDefinition, DumpDefinition, InfoEntity, TypeObject, InfoObjectStatusDefinition>> getDumps() {
        return this.dumps;
    }

    public List<Function6<UUID, UUID, InfoEntity, TypeObject, InfoObjectStatusDefinition, Long, Object[]>> getOpens() {
        return this.opens;
    }

    public List<Consumer3<InfoEntity, TypeObject, InfoObjectStatusDefinition>> getCloses() {
        return closes;
    }

    public List<Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoObjectStatusDefinition, UUID, Identification>> getCreateChildAndOpens() {
        return this.createChildAndOpens;
    }

    public List<Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoObjectStatusDefinition, Identification, InfoObjectStatusOpenDefinition>> getGetOrRebuildChilds() {
        return this.getOrRebuildChilds;
    }

    public List<Consumer4<InfoEntity, TypeObject, InfoObjectStatusDefinition, Identification>> getDeleteChilds() {
        return this.deleteChilds;
    }

    public List<Function5<Set<InfoSummaryDefinition>, Set<InfoSummaryDefinition>, InfoEntity, TypeObject, InfoObjectStatusDefinition, Predicate<InfoSummaryDefinition>>> getQueryChilds() {
        return this.queryChilds;
    }

    public List<Consumer5<InfoEntity, TypeObject, InfoObjectStatusDefinition, Identification, Identification>> getRenameChilds() {
        return this.renameChilds;
    }

    public Function3<SecurityDescriptorObject, InfoEntity, TypeObject, InfoObjectStatusDefinition> getSecurityDescriptor() {
        return this.securityDescriptor;
    }

    public void setSecurityDescriptor(Function3<SecurityDescriptorObject, InfoEntity, TypeObject, InfoObjectStatusDefinition> securityDescriptor) {
        this.securityDescriptor = securityDescriptor;
    }

    public List<Function4<Map<String, String>, Map<String, String>, InfoEntity, TypeObject, InfoObjectStatusDefinition>> getReadProperties() {
        return this.readProperties;
    }

    public List<Consumer4<InfoEntity, TypeObject, InfoObjectStatusDefinition, Map<String, String>>> getWriteProperties() {
        return this.writeProperties;
    }

    public List<Function4<byte[], byte[], InfoEntity, TypeObject, InfoObjectStatusDefinition>> getReadContents() {
        return this.readContents;
    }

    public List<Consumer4<InfoEntity, TypeObject, InfoObjectStatusDefinition, byte[]>> getWriteContents() {
        return this.writeContents;
    }
}