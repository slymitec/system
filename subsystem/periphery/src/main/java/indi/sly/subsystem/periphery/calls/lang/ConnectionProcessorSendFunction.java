package indi.sly.subsystem.periphery.calls.lang;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContentResponseDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContextRequestDefinition;
import indi.sly.system.common.lang.Function4;

import java.util.concurrent.Future;

@FunctionalInterface
public interface ConnectionProcessorSendFunction extends Function4<Future<UserContentResponseDefinition>, ConnectionDefinition, ConnectionStatusDefinition,
        UserContextRequestDefinition, Future<UserContentResponseDefinition>> {
}
