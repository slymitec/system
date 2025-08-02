package indi.sly.subsystem.periphery.calls.prototypes;

import indi.sly.subsystem.periphery.calls.instances.prototypes.processors.AConnectionInitializer;
import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.subsystem.periphery.core.prototypes.ABuilder;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectionBuilder extends ABuilder {
    protected CallFactory factory;

    public void create(String name, long attribute, String address, AConnectionInitializer initializer) {
        if (StringUtil.isNameIllegal(name) || ValueUtil.isAnyNullOrEmpty(address) || ObjectUtil.isAnyNull(initializer)) {
            throw new ConditionParametersException();
        }

        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();

        if (kernelSpace.getNamedConnectionIDs().containsKey(name)) {
            throw new StatusAlreadyExistedException();
        }

        ConnectionDefinition connection = new ConnectionDefinition();

        connection.setID(UUIDUtil.createRandom());
        connection.setAttribute(attribute);
        connection.setName(name);
        connection.setAddress(address);
        connection.setInitializer(initializer);

        kernelSpace.getConnections().put(connection.getID(), connection);
        kernelSpace.getNamedConnectionIDs().put(connection.getName(), connection.getID());
    }

    public void delete(String name) {
        if (ValueUtil.isAnyNullOrEmpty(name)) {
            throw new ConditionParametersException();
        }

        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();

        UUID connectionID = kernelSpace.getNamedConnectionIDs().getOrDefault(name, null);

        if (ValueUtil.isAnyNullOrEmpty(connectionID)) {
            throw new StatusNotExistedException();
        }

        ConnectionDefinition connection = kernelSpace.getConnections().getOrDefault(connectionID, null);

        kernelSpace.getConnections().remove(connection.getID());
        kernelSpace.getNamedConnectionIDs().remove(connection.getName());
    }
}
