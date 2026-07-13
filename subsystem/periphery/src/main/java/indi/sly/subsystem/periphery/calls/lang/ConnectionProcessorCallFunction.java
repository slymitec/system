package indi.sly.subsystem.periphery.calls.lang;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.subsystem.periphery.calls.values.ClientResponseRecord;
import indi.sly.subsystem.periphery.calls.values.ClientRequestRecord;
import indi.sly.system.common.lang.Function4;

@FunctionalInterface
public interface ConnectionProcessorCallFunction extends Function4<ClientResponseRecord, ConnectionDefinition, ConnectionStatusDefinition,
        ClientRequestRecord, ClientResponseRecord> {
}
