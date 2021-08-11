package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;

//@Named
//@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoNullResolver extends AResolver implements IInfoResolver {
    public InfoNullResolver() {
        this.dump = (dump, info, type, status) -> dump;

        this.open = (handle, info, type, status, openAttribute, arguments) -> handle;

        this.close = (info, type, status) -> {
        };

        this.createChildAndOpen = (childInfo, info, type, status, childType, identification) -> childInfo;

        this.getOrRebuildChild = (childInfo, info, type, status, identification, open) -> childInfo;

        this.deleteChild = (info, type, status, identification) -> {
        };

        this.queryChild = (summaryDefinitions, info, type, status, wildcard) -> summaryDefinitions;

        this.renameChild = (info, type, status, oldIdentification, newIdentification) -> {
        };

        this.readProperties = (properties, info, type, status) -> properties;

        this.writeProperties = (info, type, status, properties) -> {
        };

        this.readContent = (content, info, type, status) -> content;

        this.writeContent = (info, type, status, content) -> {
        };

        this.executeContent = (info, type, status) -> {
        };
    }

    private final InfoProcessorDumpFunction dump;
    private final InfoProcessorOpenFunction open;
    private final InfoProcessorCloseConsumer close;
    private final InfoProcessorCreateChildAndOpenFunction createChildAndOpen;
    private final InfoProcessorGetOrRebuildChildFunction getOrRebuildChild;
    private final InfoProcessorDeleteChildConsumer deleteChild;
    private final InfoProcessorQueryChildFunction queryChild;
    private final InfoProcessorRenameChildConsumer renameChild;
    private final InfoProcessorReadPropertyFunction readProperties;
    private final InfoProcessorWritePropertyConsumer writeProperties;
    private final InfoProcessorReadContentFunction readContent;
    private final InfoProcessorWriteContentConsumer writeContent;
    private final InfoProcessorExecuteContentConsumer executeContent;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getDumps().add(this.dump);
        processorMediator.getOpens().add(this.open);
        processorMediator.getCloses().add(this.close);
        processorMediator.getCreateChildAndOpens().add(this.createChildAndOpen);
        processorMediator.getGetOrRebuildChilds().add(this.getOrRebuildChild);
        processorMediator.getDeleteChilds().add(this.deleteChild);
        processorMediator.getQueryChilds().add(this.queryChild);
        processorMediator.getRenameChilds().add(this.renameChild);
        processorMediator.getReadProperties().add(this.readProperties);
        processorMediator.getWriteProperties().add(this.writeProperties);
        processorMediator.getReadContents().add(this.readContent);
        processorMediator.getWriteContents().add(this.writeContent);
        processorMediator.getExecuteContents().add(this.executeContent);
    }

    @Override
    public int order() {
        return 0;
    }
}
