package indi.sly.subsystem.periphery.core.environment.containers;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named
@Singleton
public class KernelSpace extends ASystemSpace {
    public KernelSpace() {
        this.configuration = new PeripheryConfiguration();
        this.userSpace = new UserSpace();
    }

    private final PeripheryConfiguration configuration;
    private final UserSpace userSpace;


    public PeripheryConfiguration getConfiguration() {
        return configuration;
    }

    public UserSpace getUserSpace() {
        return this.userSpace;
    }
}
