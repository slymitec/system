package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.functions.Consumer;
import indi.sly.system.common.functions.Provider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AValueProcessPrototype2<T1, T2> extends ACoreProcessPrototype {
    private Provider<T1> funcRead1;
    private Consumer<T1> funcWrite1;
    private Provider<T2> funcRead2;
    private Consumer<T2> funcWrite2;
    protected T1 value1;
    protected T2 value2;

    public final void setSource(Provider<T1> funcRead1, Consumer<T1> funcWrite1,
                                Provider<T2> funcRead2, Consumer<T2> funcWrite2) {
        this.funcRead1 = funcRead1;
        this.funcWrite1 = funcWrite1;
        this.funcRead2 = funcRead2;
        this.funcWrite2 = funcWrite2;
    }

    protected final void init() {
        T1 value1 = this.funcRead1.acquire();
        T2 value2 = this.funcRead2.acquire();
        this.read1(value1);
        this.read2(value2);
    }

    protected final void fresh() {
        T1 value1 = this.write1();
        T2 value2 = this.write2();
        this.funcWrite1.accept(value1);
        this.funcWrite2.accept(value2);
    }

    protected void read1(T1 source1) {
        this.value1 = source1;
    }

    protected void read2(T2 source2) {
        this.value2 = source2;
    }

    protected T1 write1() {
        return this.value1;
    }

    protected T2 write2() {
        return this.value2;
    }
}
