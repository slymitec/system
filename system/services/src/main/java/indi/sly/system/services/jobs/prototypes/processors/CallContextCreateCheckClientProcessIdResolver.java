package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.jobs.lang.CallContextProcessorCreateFunction;
import indi.sly.system.services.jobs.prototypes.mediators.CallContextProcessorMediator;
import indi.sly.system.services.jobs.values.CallContextRecord;
import indi.sly.system.services.jobs.values.CallContextProcessType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CallContextCreateCheckClientProcessIdResolver extends AResolver implements ICallContextCreateResolver {
    public CallContextCreateCheckClientProcessIdResolver() {
        this.create = (callContext, callContextRequest) -> {
            if (ObjectUtil.isNull(callContextRequest)) {
                throw new ConditionRefuseException();
            }

            UUID processId = callContextRequest.processId();

            if (ValueUtil.isAnyNullOrEmpty(processId)) {
                throw new ConditionRefuseException();
            }

            long clientType = callContextRequest.processType();
            if (LogicalUtil.isAnyEqual(clientType, CallContextProcessType.CLIENT)) {
                // Check
            } else if (LogicalUtil.isAnyEqual(clientType, CallContextProcessType.APPLICATION)) {
                // Check
            } else {
                throw new ConditionRefuseException();
            }

            return callContext;
        };
    }

    private final CallContextProcessorCreateFunction create;

    @Override
    public void resolve(CallContextProcessorMediator processorMediator) {
        processorMediator.getCreates().add(this.create);
    }

    @Override
    public int order() {
        return 0;
    }
}
