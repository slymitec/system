package indi.sly.system.kernel.objects.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.lang.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoProcessorMediator extends APrototype {
    public InfoProcessorMediator() {
        this.dumps = new HashSet<>();
        this.opens = new HashSet<>();
        this.closes = new HashSet<>();
        this.createChildAndOpens = new HashSet<>();
        this.getOrRebuildChilds = new HashSet<>();
        this.deleteChilds = new HashSet<>();
        this.queryChilds = new HashSet<>();
        this.renameChilds = new HashSet<>();
        this.readProperties = new HashSet<>();
        this.writeProperties = new HashSet<>();
        this.readContents = new HashSet<>();
        this.writeContents = new HashSet<>();
        this.executeContents = new HashSet<>();
    }

    private InfoSelfFunction self;
    private InfoParentFunction parent;
    private SecurityDescriptorFunction securityDescriptor;
    private final Set<DumpFunction> dumps;
    private final Set<OpenFunction> opens;
    private final Set<CloseConsumer> closes;
    private final Set<CreateChildAndOpenFunction> createChildAndOpens;
    private final Set<GetOrRebuildChildFunction> getOrRebuildChilds;
    private final Set<DeleteChildConsumer> deleteChilds;
    private final Set<QueryChildFunction> queryChilds;
    private final Set<RenameChildConsumer> renameChilds;
    private final Set<ReadPropertyFunction> readProperties;
    private final Set<WritePropertyConsumer> writeProperties;
    private final Set<ReadContentFunction> readContents;
    private final Set<WriteContentConsumer> writeContents;
    private final Set<ExecuteContentConsumer> executeContents;

    public InfoSelfFunction getSelf() {
        return this.self;
    }

    public void setSelf(InfoSelfFunction self) {
        this.self = self;
    }

    public InfoParentFunction getParent() {
        return this.parent;
    }

    public void setParent(InfoParentFunction parent) {
        this.parent = parent;
    }

    public SecurityDescriptorFunction getSecurityDescriptor() {
        return this.securityDescriptor;
    }

    public void setSecurityDescriptor(SecurityDescriptorFunction securityDescriptor) {
        this.securityDescriptor = securityDescriptor;
    }

    public Set<DumpFunction> getDumps() {
        return this.dumps;
    }

    public Set<OpenFunction> getOpens() {
        return this.opens;
    }

    public Set<CloseConsumer> getCloses() {
        return this.closes;
    }

    public Set<CreateChildAndOpenFunction> getCreateChildAndOpens() {
        return this.createChildAndOpens;
    }

    public Set<GetOrRebuildChildFunction> getGetOrRebuildChilds() {
        return this.getOrRebuildChilds;
    }

    public Set<DeleteChildConsumer> getDeleteChilds() {
        return this.deleteChilds;
    }

    public Set<QueryChildFunction> getQueryChilds() {
        return this.queryChilds;
    }

    public Set<RenameChildConsumer> getRenameChilds() {
        return this.renameChilds;
    }

    public Set<ReadPropertyFunction> getReadProperties() {
        return this.readProperties;
    }

    public Set<WritePropertyConsumer> getWriteProperties() {
        return this.writeProperties;
    }

    public Set<ReadContentFunction> getReadContents() {
        return this.readContents;
    }

    public Set<WriteContentConsumer> getWriteContents() {
        return this.writeContents;
    }

    public Set<ExecuteContentConsumer> getExecuteContents() {
        return this.executeContents;
    }
}