package indi.sly.system.kernel.objects.prototypes.wrappers;

import java.util.*;
import java.util.function.Predicate;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;

public class InfoProcessorMediator {
    public InfoProcessorMediator() {
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

    private InfoFunction info;
    private ParentFunction parent;
    private SecurityDescriptorFunction securityDescriptor;
    private final List<DumpFunction> dumps;
    private final List<OpenFunction> opens;
    private final List<CloseConsumer> closes;
    private final List<CreateChildAndOpenFunction> createChildAndOpens;
    private final List<GetOrRebuildChildFunction> getOrRebuildChilds;
    private final List<DeleteChildConsumer> deleteChilds;
    private final List<QueryChildFunction> queryChilds;
    private final List<RenameChildConsumer> renameChilds;
    private final List<ReadPropertyFunction> readProperties;
    private final List<WritePropertyConsumer> writeProperties;
    private final List<ReadContentFunction> readContents;
    private final List<WriteContentConsumer> writeContents;

    public InfoFunction getInfo() {
        return this.info;
    }

    public void setInfo(InfoFunction info) {
        this.info = info;
    }

    public ParentFunction getParent() {
        return this.parent;
    }

    public void setParent(ParentFunction parent) {
        this.parent = parent;
    }

    public SecurityDescriptorFunction getSecurityDescriptor() {
        return this.securityDescriptor;
    }

    public void setSecurityDescriptor(SecurityDescriptorFunction securityDescriptor) {
        this.securityDescriptor = securityDescriptor;
    }

    public List<DumpFunction> getDumps() {
        return this.dumps;
    }

    public List<OpenFunction> getOpens() {
        return this.opens;
    }

    public List<CloseConsumer> getCloses() {
        return this.closes;
    }

    public List<CreateChildAndOpenFunction> getCreateChildAndOpens() {
        return this.createChildAndOpens;
    }

    public List<GetOrRebuildChildFunction> getGetOrRebuildChilds() {
        return this.getOrRebuildChilds;
    }

    public List<DeleteChildConsumer> getDeleteChilds() {
        return this.deleteChilds;
    }

    public List<QueryChildFunction> getQueryChilds() {
        return this.queryChilds;
    }

    public List<RenameChildConsumer> getRenameChilds() {
        return this.renameChilds;
    }

    public List<ReadPropertyFunction> getReadProperties() {
        return this.readProperties;
    }

    public List<WritePropertyConsumer> getWriteProperties() {
        return this.writeProperties;
    }

    public List<ReadContentFunction> getReadContents() {
        return this.readContents;
    }

    public List<WriteContentConsumer> getWriteContents() {
        return this.writeContents;
    }
}