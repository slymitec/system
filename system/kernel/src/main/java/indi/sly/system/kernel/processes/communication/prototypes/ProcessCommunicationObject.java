package indi.sly.system.kernel.processes.communication.prototypes;

import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCommunicationObject extends ABytesProcessObject {
    @Override
    protected void read(byte[] source) {
    }

    @Override
    protected byte[] write() {
        return new byte[0];
    }
}
