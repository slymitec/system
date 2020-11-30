package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.exceptions.AKernelException;
import indi.sly.system.common.functions.Consumer;
import indi.sly.system.common.functions.Provider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ABytesProcessObject extends ACoreProcessObject {
    private Provider<byte[]> funcRead;
    private Consumer<byte[]> funcWrite;

    public final void setSource(Provider<byte[]> funcRead, Consumer<byte[]> funcWrite) {
        this.funcRead = funcRead;
        this.funcWrite = funcWrite;
    }

    protected final void init() {
        byte[] value = this.funcRead.acquire();
        this.read(value);
    }

    protected final void fresh() {
        byte[] value = this.write();
        this.funcWrite.accept(value);
    }

    protected abstract void read(byte[] source);

    protected abstract byte[] write();
}
