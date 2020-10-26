package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.functions.Consumer;
import indi.sly.system.common.functions.Provider;
import indi.sly.system.kernel.core.ACoreObject;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AInfoContentObject extends ACoreObject {
    private Provider<byte[]> funcRead;
    private Consumer<byte[]> funcWrite;
    protected Consumer<Long> funcLock;

    public final void setSource(Provider<byte[]> funcRead, Consumer<byte[]> funcWrite) {
        this.funcRead = funcRead;
        this.funcWrite = funcWrite;
    }

    public final void setSource(InfoEntity info) {
        this.funcRead = info::getContent;
        this.funcWrite = info::setContent;
    }

    public void setLock(Consumer<Long> funcLock) {
        this.funcLock = funcLock;
    }

    public final void init() {
        if (this.funcLock == null) {
            this.funcLock = (lockMode) -> {
            };
        }

        byte[] value = this.funcRead.acquire();
        this.read(value);
    }

    public final void fresh() {
        byte[] value = this.write();
        this.funcWrite.accept(value);
    }

    protected abstract void read(byte[] value);

    protected abstract byte[] write();
}
