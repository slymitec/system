package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.services.core.prototypes.TransactionalActionObject;
import indi.sly.system.services.job.instances.prototypes.processors.AJobInitializer;
import indi.sly.system.services.job.lang.*;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobAttributeType;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobInitializerRunSummaryDefinition;
import indi.sly.system.services.core.values.TransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobInitializerResolver extends AJobResolver {
    public JobInitializerResolver() {
        this.start = (job, status) -> {
            AJobInitializer initializer = job.getInitializer();

            initializer.start(job);
        };

        this.finish = (job, status) -> {
            AJobInitializer initializer = job.getInitializer();

            initializer.finish(job);
        };

        this.run = (job, status, name, run, content) -> {
            AJobInitializer initializer = job.getInitializer();
            JobInitializerRunSummaryDefinition initializerRun = initializer.getRun(name);

            try {
                long initializerRunTransaction = TransactionType.WHATEVER;
                if (!LogicalUtil.isAnyExist(job.getAttribute(), JobAttributeType.HAS_NOT_TRANSACTION)) {
                    initializerRunTransaction = initializerRun.getTransaction();
                }

                TransactionalActionObject transactionalAction = this.factoryManager.create(TransactionalActionObject.class);
                transactionalAction.run(initializerRunTransaction, (Provider<Void>) () -> {
                    initializerRun.getMethod().accept(run, content);

                    return null;
                });
            } catch (AKernelException exception) {
                content.setException(exception);
            }
        };
    }

    @Override
    public int order() {
        return 3;
    }

    private final JobProcessorStartFunction start;
    private final JobProcessorFinishConsumer finish;
    private final JobProcessorRunConsumer run;

    @Transactional(value = Transactional.TxType.SUPPORTS)
    protected void runEntry(JobInitializerRunMethodConsumer initializerRunMethodEntry,
                            JobRunConsumer run, JobContentObject content) {
        initializerRunMethodEntry.accept(run, content);
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    protected void runEntryWithIndependentTransactional(JobInitializerRunMethodConsumer initializerRunMethodEntry,
                                                        JobRunConsumer run, JobContentObject content) {
        initializerRunMethodEntry.accept(run, content);
    }

    @Transactional(value = Transactional.TxType.NOT_SUPPORTED)
    protected void runEntryWithoutTransactional(JobInitializerRunMethodConsumer initializerRunMethodEntry,
                                                JobRunConsumer run, JobContentObject content) {
        initializerRunMethodEntry.accept(run, content);
    }

    @Override
    public void resolve(JobDefinition job, JobProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
        processorMediator.getRuns().add(this.run);
    }
}
