package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.job.lang.*;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobAttributeType;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobInitializerRunSummaryDefinition;
import indi.sly.system.services.job.values.JobTransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobInitializerResolver extends AResolver implements IJobResolver {
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
                long initializerRunTransaction = JobTransactionType.WHATEVER;
                if (LogicalUtil.isNotAnyExist(job.getAttribute(), JobAttributeType.HAS_NOT_TRANSACTION)) {
                    initializerRunTransaction = initializerRun.getTransaction();
                }

                if (initializerRunTransaction == JobTransactionType.INDEPENDENCE) {
                    this.runEntryWithIndependentTransactional(initializerRun.getMethod(), run, content);
                } else if (initializerRunTransaction == JobTransactionType.PROHIBITED) {
                    this.runEntryWithoutTransactional(initializerRun.getMethod(), run, content);
                } else if (initializerRunTransaction == JobTransactionType.WHATEVER) {
                    this.runEntry(initializerRun.getMethod(), run, content);
                }
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
