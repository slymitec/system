package indi.sly.system.kernel.objects.prototypes;


import com.sun.xml.bind.v2.schemagen.xmlschema.TypeHost;
import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.functions.Consumer;
import indi.sly.system.common.functions.Provider;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.ACoreObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ABytesProcessObject extends ACoreObject {
    private Provider<byte[]> funcRead;
    private Consumer<byte[]> funcWrite;
    private Consumer<Long> funcLock;

    public final void setSource(Provider<byte[]> funcRead, Consumer<byte[]> funcWrite) {
        this.funcRead = funcRead;
        this.funcWrite = funcWrite;
    }

    public final void setLock(Consumer<Long> funcLock) {
        this.funcLock = funcLock;
    }

    protected final void lock(long lockType) {
        if (ObjectUtils.isAnyNull(this.funcLock)) {
            throw new StatusNotSupportedException();
        }

        this.funcLock.accept(lockType);
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
