package indi.sly.system.services.core.prototypes;

import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.core.values.TransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TransactionalActionObject extends APrototype {
    public <R> R run(long type, Provider<R> provider) {
        if (LogicalUtil.isAnyEqual(type, TransactionType.INDEPENDENCE)) {
            return this.runWithIndependentTransactional(provider);
        } else if (LogicalUtil.isAnyEqual(type, TransactionType.PROHIBITED)) {
            return this.runWithoutTransactional(provider);
        } else if (LogicalUtil.isAnyEqual(type, TransactionType.WHATEVER)) {
            return this.runWithWhatever(provider);
        } else {
            return provider.acquire();
        }
    }

    @Transactional(value = Transactional.TxType.SUPPORTS)
    public <R> R runWithWhatever(Provider<R> provider) {
        return provider.acquire();
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public <R> R runWithIndependentTransactional(Provider<R> provider) {
        return provider.acquire();
    }

    @Transactional(value = Transactional.TxType.NOT_SUPPORTED)
    public <R> R runWithoutTransactional(Provider<R> provider) {
        return provider.acquire();
    }
}
