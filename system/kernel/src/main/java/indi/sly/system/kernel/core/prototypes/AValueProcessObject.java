package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.functions.Consumer;
import indi.sly.system.common.functions.Provider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AValueProcessObject<T> extends ACoreProcessObject {
    private Provider<T> funcRead;
    private Consumer<T> funcWrite;
    protected T value;

    public final void setSource(Provider<T> funcRead, Consumer<T> funcWrite) {
        this.funcRead = funcRead;
        this.funcWrite = funcWrite;
    }

    protected final void init() {
        T value = this.funcRead.acquire();
        this.read(value);
    }

    protected final void fresh() {
        T value = this.write();
        this.funcWrite.accept(value);
    }

    protected void read(T source) {
        this.value = source;
    }

    protected T write() {
        return this.value;
    }
}
