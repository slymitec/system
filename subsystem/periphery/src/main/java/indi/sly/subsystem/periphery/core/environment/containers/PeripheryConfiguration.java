package indi.sly.subsystem.periphery.core.environment.containers;

import indi.sly.system.common.containers.AConfiguration;
import indi.sly.system.common.supports.UUIDUtil;

import java.util.UUID;

public class PeripheryConfiguration extends AConfiguration {
    public final String CALL_CONNECTION_INSTANCE_SYSTEM_NAME = "System";
    public final String CALL_CONNECTION_INSTANCE_SYSTEM_ADDRESS = "http://localhost:8080/Call.action";

    public final String CALL_CONNECTION_INSTANCE_SYSTEM_NAME_WEBSOCKET = "System_WebSocket";
    public final String CALL_CONNECTION_INSTANCE_SYSTEM_ADDRESS_WEBSOCKET = "ws://localhost:8080/InterActive.action";
}
