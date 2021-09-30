package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessSessionObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.values.AccountAuthorizationSummaryDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateSessionResolver extends AProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateSessionResolver() {
        this.create = (process, parentProcess, processCreator) -> {
            ProcessSessionObject parentProcessSession = parentProcess.getSession();
            ProcessSessionObject processSession = process.getSession();


            String sessionName = processCreator.getSessionName();

            if (!StringUtil.isNameIllegal(sessionName)) {
                AccountAuthorizationObject accountAuthorization = processCreator.getAccountAuthorization();

                if (ObjectUtil.allNotNull(accountAuthorization) && accountAuthorization.isLegal()) {
                    AccountAuthorizationSummaryDefinition accountAuthorizationSummary = accountAuthorization.checkAndGetSummary();


                    if(!accountAuthorizationSummary.getSessionNames().contains(sessionName)){
                        throw new StatusNotExistedException();
                    }

                } else {

                }
            }

            if (!ValueUtil.isAnyNullOrEmpty(parentProcessSession.getID())) {
                processSession.inheritID();
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(create);
    }
}
