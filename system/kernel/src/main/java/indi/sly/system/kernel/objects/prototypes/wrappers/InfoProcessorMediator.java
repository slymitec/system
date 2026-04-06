package indi.sly.system.kernel.objects.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.wrappers.AMediator;
import indi.sly.system.kernel.objects.lang.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoProcessorMediator extends AMediator {
    public InfoProcessorMediator() {
        this.dumps = new ArrayList<>();
        this.opens = new ArrayList<>();
        this.closes = new ArrayList<>();
        this.createChildren = new ArrayList<>();
        this.getOrRebuildChildren = new ArrayList<>();
        this.deleteChildren = new ArrayList<>();
        this.queryChildren = new ArrayList<>();
        this.renameChildren = new ArrayList<>();
        this.readProperties = new ArrayList<>();
        this.writeProperties = new ArrayList<>();
        this.readContents = new ArrayList<>();
        this.writeContents = new ArrayList<>();
        this.executeContents = new ArrayList<>();
    }

    private InfoProcessorSelfFunction self;
    private InfoProcessorParentFunction parent;
    private InfoProcessorSecurityDescriptorFunction securityDescriptor;
    private final List<InfoProcessorDumpFunction> dumps;
    private final List<InfoProcessorOpenFunction> opens;
    private final List<InfoProcessorCloseFunction> closes;
    private final List<InfoProcessorCreateChildFunction> createChildren;
    private final List<InfoProcessorGetOrRebuildChildFunction> getOrRebuildChildren;
    private final List<InfoProcessorDeleteChildConsumer> deleteChildren;
    private final List<InfoProcessorQueryChildFunction> queryChildren;
    private final List<InfoProcessorRenameChildConsumer> renameChildren;
    private final List<InfoProcessorReadPropertyFunction> readProperties;
    private final List<InfoProcessorWritePropertyConsumer> writeProperties;
    private final List<InfoProcessorReadContentFunction> readContents;
    private final List<InfoProcessorWriteContentConsumer> writeContents;
    private final List<InfoProcessorExecuteContentConsumer> executeContents;

    public InfoProcessorSelfFunction getSelf() {
        return this.self;
    }

    public void setSelf(InfoProcessorSelfFunction self) {
        this.self = self;
    }

    public InfoProcessorParentFunction getParent() {
        return this.parent;
    }

    public void setParent(InfoProcessorParentFunction parent) {
        this.parent = parent;
    }

    public InfoProcessorSecurityDescriptorFunction getSecurityDescriptor() {
        return this.securityDescriptor;
    }

    public void setSecurityDescriptor(InfoProcessorSecurityDescriptorFunction securityDescriptor) {
        this.securityDescriptor = securityDescriptor;
    }

    public List<InfoProcessorDumpFunction> getDumps() {
        return this.dumps;
    }

    public List<InfoProcessorOpenFunction> getOpens() {
        return this.opens;
    }

    public List<InfoProcessorCloseFunction> getCloses() {
        return this.closes;
    }

    public List<InfoProcessorCreateChildFunction> getCreateChildren() {
        return this.createChildren;
    }

    public List<InfoProcessorGetOrRebuildChildFunction> getGetOrRebuildChildren() {
        return this.getOrRebuildChildren;
    }

    public List<InfoProcessorDeleteChildConsumer> getDeleteChildren() {
        return this.deleteChildren;
    }

    public List<InfoProcessorQueryChildFunction> getQueryChildren() {
        return this.queryChildren;
    }

    public List<InfoProcessorRenameChildConsumer> getRenameChildren() {
        return this.renameChildren;
    }

    public List<InfoProcessorReadPropertyFunction> getReadProperties() {
        return this.readProperties;
    }

    public List<InfoProcessorWritePropertyConsumer> getWriteProperties() {
        return this.writeProperties;
    }

    public List<InfoProcessorReadContentFunction> getReadContents() {
        return this.readContents;
    }

    public List<InfoProcessorWriteContentConsumer> getWriteContents() {
        return this.writeContents;
    }

    public List<InfoProcessorExecuteContentConsumer> getExecuteContents() {
        return this.executeContents;
    }
}