package indi.sly.system.services.center.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.services.center.lang.CenterInitializerRunMethodConsumer;

public class CenterInitializerRunDefinition extends ADefinition<CenterInitializerRunDefinition> {
    public CenterInitializerRunDefinition() {
        this.transaction = CenterTransactionType.INDEPENDENCE;
    }

    private CenterInitializerRunMethodConsumer method;
    private long transaction;

    public CenterInitializerRunMethodConsumer getMethod() {
        return this.method;
    }

    public void setMethod(CenterInitializerRunMethodConsumer method) {
        this.method = method;
    }

    public long getTransaction() {
        return this.transaction;
    }

    public void setTransaction(long transaction) {
        this.transaction = transaction;
    }
}
