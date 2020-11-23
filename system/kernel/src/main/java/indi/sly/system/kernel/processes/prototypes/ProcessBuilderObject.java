package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.core.prototypes.ACoreObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessBuilderObject extends ACoreObject {
    private ProcessObjectFactoryObject processObjectFactory;

    public void setProcessObjectFactory(ProcessObjectFactoryObject processObjectFactory) {
        this.processObjectFactory = processObjectFactory;
    }

    public void setFileHandle(UUID handle) {


        //return this;
    }


    public ProcessObject build() {
        ProcessObject process = this.processObjectFactory.buildProcessObject(null);

        return null;
    }
}
