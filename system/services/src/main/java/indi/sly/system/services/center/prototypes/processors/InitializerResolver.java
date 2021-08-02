package indi.sly.system.services.center.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.center.lang.*;
import indi.sly.system.services.center.prototypes.CenterContentObject;
import indi.sly.system.services.center.prototypes.wrappers.CenterProcessorMediator;
import indi.sly.system.services.center.values.CenterAttributeType;
import indi.sly.system.services.center.values.CenterDefinition;
import indi.sly.system.services.center.values.CenterTransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InitializerResolver extends APrototype implements ICenterResolver {
    public InitializerResolver() {
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
            Map<String, InitializerConsumer> initializerRunMethods = initializer.getRunMethods();
            Map<String, Long> initializerRunTransactions = initializer.getRunTransactions();

            InitializerConsumer initializerRunEntry = initializerRunMethods.getOrDefault(name, null);

            if (ObjectUtil.isAnyNull(initializerRunEntry)) {
                throw new StatusNotExistedException();
            }

            try {
                long initializerRunTransaction = CenterTransactionType.WHATEVER;
                if (LogicalUtil.isNotAnyExist(center.getAttribute(), CenterAttributeType.HAS_NOT_TRANSACTION)) {
                    initializerRunTransaction = initializerRunTransactions.getOrDefault(name,
                            CenterTransactionType.INDEPENDENCE);
                }

                if (initializerRunTransaction == CenterTransactionType.INDEPENDENCE) {
                    this.runEntryWithIndependentTransactional(initializerRunEntry, run, content);
                } else if (initializerRunTransaction == CenterTransactionType.PROHIBITED) {
                    this.runEntryWithoutTransactional(initializerRunEntry, run, content);
                } else if (initializerRunTransaction == CenterTransactionType.WHATEVER) {
                    this.runEntry(initializerRunEntry, run, content);
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

    private final StartFunction start;
    private final FinishConsumer finish;
    private final RunConsumer run;

    @Transactional(value = Transactional.TxType.SUPPORTS)
    protected void runEntry(InitializerConsumer initializerRunEntry, RunSelfConsumer run,
                            CenterContentObject content) {
        initializerRunEntry.accept(run, content);
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    protected void runEntryWithIndependentTransactional(InitializerConsumer initializerRunEntry, RunSelfConsumer run,
                                                        CenterContentObject content) {
        initializerRunEntry.accept(run, content);
    }

    @Transactional(value = Transactional.TxType.NOT_SUPPORTED)
    protected void runEntryWithoutTransactional(InitializerConsumer initializerRunEntry, RunSelfConsumer run,
                                                CenterContentObject content) {
        initializerRunEntry.accept(run, content);
    }

    @Override
    public void resolve(CenterDefinition center, CenterProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
        processorMediator.getRuns().add(this.run);
    }
}
