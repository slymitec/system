package indi.sly.subsystem.periphery.calls.lang;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContentResponseDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContextRequestRawDefinition;
import indi.sly.system.common.lang.Function4;

@FunctionalInterface
public interface ConnectionProcessorSendFunction extends Function4<UserContentResponseDefinition, ConnectionDefinition, ConnectionStatusDefinition,
        UserContextRequestRawDefinition, UserContentResponseDefinition> {
}
