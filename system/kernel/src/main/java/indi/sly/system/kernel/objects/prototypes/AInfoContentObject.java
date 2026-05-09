package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.AChildCacheableObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.lang.InfoProcessorExecuteContentConsumer;
import indi.sly.system.kernel.objects.lang.InfoProcessorReadContentFunction;
import indi.sly.system.kernel.objects.lang.InfoProcessorWriteContentConsumer;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AInfoContentObject extends AChildCacheableObject<InfoContentCacheEntity, InfoObject> {
    protected InfoProcessorMediator processorMediator;

    private InfoEntity getSelf() {
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getInfo().getInfoId())) {
            throw new ConditionContextException();
        }

        return this.processorMediator.getSelf().apply(this.cache.getInfo());
    }

    public byte[] read() {
        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.base.getType());

        InfoEntity info = this.getSelf();

        List<InfoProcessorReadContentFunction> resolvers = this.base.processorMediator.getReadContents();

        byte[] source = null;

        for (InfoProcessorReadContentFunction resolver : resolvers) {
            source = resolver.apply(source, info, type, this.cache.getInfo());
        }

        return source;
    }

    public void write(byte[] source) {
        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.base.getType());

        InfoEntity info = this.getSelf();

        List<InfoProcessorWriteContentConsumer> resolvers = this.base.processorMediator.getWriteContents();

        for (InfoProcessorWriteContentConsumer resolver : resolvers) {
            resolver.accept(info, type, this.cache.getInfo(), source);
        }
    }

    public void execute() {
        TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
        TypeObject type = typeManager.get(this.base.getType());

        InfoEntity info = this.getSelf();

        List<InfoProcessorExecuteContentConsumer> resolvers = this.base.processorMediator.getExecuteContents();

        for (InfoProcessorExecuteContentConsumer resolver : resolvers) {
            resolver.accept(info, type, this.cache.getInfo());
        }
    }
}
