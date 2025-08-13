package indi.sly.subsystem.periphery.calls.lang;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContentResponseDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContextRequestDefinition;
import indi.sly.system.common.lang.Function4;

@FunctionalInterface
public interface ConnectionProcessorCallFunction extends Function4<UserContentResponseDefinition, ConnectionDefinition, ConnectionStatusDefinition,
        UserContextRequestDefinition, UserContentResponseDefinition> {
}
