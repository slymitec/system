package indi.sly.subsystem.periphery.calls;

import indi.sly.subsystem.periphery.calls.instances.prototypes.processors.AConnectionInitializer;
import indi.sly.subsystem.periphery.calls.instances.prototypes.processors.WebSocketConnectionInitializer;
import indi.sly.subsystem.periphery.calls.prototypes.CallFactory;
import indi.sly.subsystem.periphery.calls.prototypes.ConnectionBuilder;
import indi.sly.subsystem.periphery.calls.prototypes.ConnectionObject;
import indi.sly.subsystem.periphery.calls.values.ConnectionAttributeType;
import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.core.AManager;
import indi.sly.subsystem.periphery.core.boot.values.StartupType;
import indi.sly.subsystem.periphery.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.subsystem.periphery.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CallManager extends AManager {
    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
            this.factory = this.factoryManager.create(CallFactory.class);
            this.factory.init();
        } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_PERIPHERY)) {
            KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
            KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

            this.createConnection(kernelConfiguration.CALL_CONNECTION_INSTANCE_SYSTEM_NAME, ConnectionAttributeType.NULL,
                    kernelConfiguration.CALL_CONNECTION_INSTANCE_SYSTEM_ADDRESS, this.factoryManager.create(WebSocketConnectionInitializer.class));

        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void check() {
    }

    protected CallFactory factory;

    public void createConnection(String name, long attribute, String address, AConnectionInitializer initializer) {
        if (StringUtil.isNameIllegal(name) || ObjectUtil.isAnyNull(initializer)) {
            throw new ConditionParametersException();
        }

        ConnectionBuilder connectBuilder = this.factory.createConnection();

        connectBuilder.create(name, attribute, address, initializer);
    }

    public void deleteConnection(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        ConnectionBuilder connectBuilder = this.factory.createConnection();

        connectBuilder.delete(name);
    }

    public ConnectionObject getConnection(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();

        UUID connectID = kernelSpace.getNamedConnectionIDs().getOrDefault(name, null);

        if (ValueUtil.isAnyNullOrEmpty(connectID)) {
            throw new StatusNotExistedException();
        }

        ConnectionDefinition connection = kernelSpace.getConnections().getOrDefault(connectID, null);

        if (ObjectUtil.isAnyNull(connection)) {
            throw new StatusNotExistedException();
        }

        return this.factory.buildConnection(connection);
    }
}
