package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.sessions.prototypes.AppContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessContextObject extends ABytesProcessObject {
    @Override
    protected void read(byte[] source) {
        this.processContext = ObjectUtils.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtils.transferToByteArray(this.processContext);
    }

    private ProcessContextDefinition processContext;

//    public Map<String, String> getEnvironmentVariable() {
//        this.init();
//
//        return processContext.getEnvironmentVariable();
//    }
//
//    public List<Identification> getWorkFolder() {
//        this.init();
//
//        return processContext.getWorkFolder();
//    }

    //need object
    public AppContextDefinition getAppContext() {
        this.init();

        return processContext.getAppContext();
    }

    public void setAppContext(AppContextDefinition appContext) {
        this.init();

        processContext.setAppContext(appContext);
    }
}