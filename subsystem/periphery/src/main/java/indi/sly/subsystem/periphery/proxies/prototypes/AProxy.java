package indi.sly.subsystem.periphery.proxies.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.ACacheableObject;
import indi.sly.subsystem.periphery.proxies.lang.ProxyInvokeFunction;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.ProxyProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.ProxyCacheEntity;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;

import java.util.Set;
import java.util.UUID;

public abstract class AProxy extends ACacheableObject<ProxyCacheEntity> {
    protected ProxyFactory factory;
    protected ProxyProcessorMediator processorMediator;

    public UUID getTargetHandle() {
        return this.cache.getHandle();
    }

    @SuppressWarnings("unchecked")
    protected <T> T invoke(String method, Class<T> returnClazz, Object... arg) {
        if (ValueUtil.isAnyNullOrEmpty(method) || ObjectUtil.isAnyNull(returnClazz)) {
            throw new ConditionParametersException();
        }

        Object returnValue = null;

        Set<ProxyInvokeFunction> invokes = this.processorMediator.getInvokes();

        for (ProxyInvokeFunction invoke : invokes) {
            returnValue = invoke.apply(returnValue, this.cache, method, returnClazz, arg);
        }

        return (T) returnValue;
    }
}
