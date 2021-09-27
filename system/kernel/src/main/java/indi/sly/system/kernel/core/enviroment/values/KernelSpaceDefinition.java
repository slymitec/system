package indi.sly.system.kernel.core.enviroment.values;

import indi.sly.system.common.lang.Provider;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Named
@Singleton
public class KernelSpaceDefinition extends ASpaceDefinition<KernelSpaceDefinition> {
    public KernelSpaceDefinition() {
        this.configuration = new KernelConfigurationDefinition();

        this.infoTypeIDs = new ConcurrentSkipListSet<>();
    }

    private final KernelConfigurationDefinition configuration;
    private final Set<UUID> infoTypeIDs;
    private Provider<UserSpaceDefinition> userSpace;
    private AKernelSpaceExtensionDefinition<?> serviceSpace;

    public KernelConfigurationDefinition getConfiguration() {
        return configuration;
    }

    public Set<UUID> getInfoTypeIDs() {
        return this.infoTypeIDs;
    }

    public Provider<UserSpaceDefinition> getUserSpace() {
        return this.userSpace;
    }

    public void setUserSpace(Provider<UserSpaceDefinition> userSpace) {
        this.userSpace = userSpace;
    }

    public AKernelSpaceExtensionDefinition<?> getServiceSpace() {
        return this.serviceSpace;
    }

    public void setServiceSpace(AKernelSpaceExtensionDefinition<?> serviceSpace) {
        this.serviceSpace = serviceSpace;
    }
}
