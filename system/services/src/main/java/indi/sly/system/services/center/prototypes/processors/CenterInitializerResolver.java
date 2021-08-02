package indi.sly.system.services.center.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.center.lang.*;
import indi.sly.system.services.center.prototypes.CenterContentObject;
import indi.sly.system.services.center.prototypes.wrappers.CenterProcessorMediator;
import indi.sly.system.services.center.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CenterInitializerResolver extends APrototype implements ICenterResolver {
    public CenterInitializerResolver() {
        this.start = (center, status) -> {
            ACenterInitializer initializer = center.getInitializer();

            initializer.start(center);
        };

        this.finish = (center, status) -> {
            ACenterInitializer initializer = center.getInitializer();

            initializer.finish(center);
        };

        this.run = (center, status, name, run, content) -> {
            ACenterInitializer initializer = center.getInitializer();
            CenterInitializerRunSummaryDefinition initializerRun = initializer.getRun(name);

            try {
                long initializerRunTransaction = CenterTransactionType.WHATEVER;
                if (LogicalUtil.isNotAnyExist(center.getAttribute(), CenterAttributeType.HAS_NOT_TRANSACTION)) {
                    initializerRunTransaction = initializerRun.getTransaction();
                }

                if (initializerRunTransaction == CenterTransactionType.INDEPENDENCE) {
                    this.runEntryWithIndependentTransactional(initializerRun.getMethod(), run, content);
                } else if (initializerRunTransaction == CenterTransactionType.PROHIBITED) {
                    this.runEntryWithoutTransactional(initializerRun.getMethod(), run, content);
                } else if (initializerRunTransaction == CenterTransactionType.WHATEVER) {
                    this.runEntry(initializerRun.getMethod(), run, content);
                }
            } catch (AKernelException exception) {
                content.setException(exception);
            }
        };
    }

    @Override
    public int order() {
        return 2;
    }

    private final CenterProcessorStartFunction start;
    private final CenterProcessorFinishConsumer finish;
    private final CenterProcessorRunConsumer run;

    @Transactional(value = Transactional.TxType.SUPPORTS)
    protected void runEntry(CenterInitializerRunMethodConsumer initializerRunMethodEntry,
                            CenterRunConsumer run, CenterContentObject content) {
        initializerRunMethodEntry.accept(run, content);
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    protected void runEntryWithIndependentTransactional(CenterInitializerRunMethodConsumer initializerRunMethodEntry,
                                                        CenterRunConsumer run, CenterContentObject content) {
        initializerRunMethodEntry.accept(run, content);
    }

    @Transactional(value = Transactional.TxType.NOT_SUPPORTED)
    protected void runEntryWithoutTransactional(CenterInitializerRunMethodConsumer initializerRunMethodEntry,
                                                CenterRunConsumer run, CenterContentObject content) {
        initializerRunMethodEntry.accept(run, content);
    }

    @Override
    public void resolve(CenterDefinition center, CenterProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
        processorMediator.getRuns().add(this.run);
    }
}
