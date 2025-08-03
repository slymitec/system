package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContentResponseDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContextRequestRawDefinition;
import indi.sly.subsystem.periphery.core.prototypes.processors.AInitializer;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AConnectionInitializer extends AInitializer {
    public AConnectionInitializer() {
    }

    public abstract void connect(ConnectionDefinition connection, ConnectionStatusDefinition status);

    public abstract void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status);

    public abstract UserContentResponseDefinition send(UserContextRequestRawDefinition userContextRequestRaw, ConnectionStatusDefinition status);
}
