package indi.sly.system.services.center.instances.prototypes.processors;

import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.processes.values.ProcessTokenLimitType;
import indi.sly.system.services.center.lang.CenterRunConsumer;
import indi.sly.system.services.center.prototypes.CenterContentObject;
import indi.sly.system.services.center.prototypes.processors.ACenterInitializer;
import indi.sly.system.services.center.values.CenterDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TestCenterInitializer extends ACenterInitializer {
    public TestCenterInitializer() {
        this.register("test", this::test);
    }

    @Override
    public void start(CenterDefinition center) {
    }

    @Override
    public void finish(CenterDefinition center) {
    }

    private void test(CenterRunConsumer run, CenterContentObject content) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();

        ProcessTokenObject processToken = process.getToken();

        Map<Long, Integer> limits = new HashMap<>(processToken.getLimits());

        limits.put(ProcessTokenLimitType.HANDLE_MAX, 65536);

        processToken.setLimits(limits);
    }


}
