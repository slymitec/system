package indi.sly.subsystem.periphery.core.enviroment.values;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named
@Singleton
public class KernelSpaceDefinition extends ASpaceDefinition<KernelSpaceDefinition> {
    public KernelSpaceDefinition() {
        this.configuration = new KernelConfigurationDefinition();
        this.userSpace = new UserSpaceDefinition();
    }

    private final KernelConfigurationDefinition configuration;

    private final UserSpaceDefinition userSpace;

    public KernelConfigurationDefinition getConfiguration() {
        return configuration;
    }

    public UserSpaceDefinition getUserSpace() {
        return this.userSpace;
    }
}
