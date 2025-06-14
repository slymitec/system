package indi.sly.system.kernel.core.boot.prototypes;

import indi.sly.system.common.lang.MethodScope;
import indi.sly.system.common.values.MethodScopeType;

public interface IStartupCapable {
    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    void startup(long startup);

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    void shutdown();
}
