package indi.sly.subsystem.periphery.calls.lang;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.system.common.lang.Consumer2;

@FunctionalInterface
public interface ConnectionProcessorDisconnectConsumer extends Consumer2<ConnectionDefinition, ConnectionStatusDefinition> {
}
