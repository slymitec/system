package indi.sly.subsystem.periphery.core.enviroment.values;

import indi.sly.system.common.values.ADefinition;

public class KernelConfigurationDefinition extends ADefinition<UserSpaceDefinition> {
    public final long CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT = 16L;

    public final String CALL_CONNECTION_SYSTEM_NAME = "System";
    public final String CALL_CONNECTION_SYSTEM_ADDRESS = "ws://localhost:8080/Call.action";
}
