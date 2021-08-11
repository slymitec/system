package indi.sly.system.kernel.core.enviroment.values;

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
        this.userSpaces = new ThreadLocal<>();
    }

    private final KernelConfigurationDefinition configuration;
    private final Set<UUID> infoTypeIDs;
    private final ThreadLocal<UserSpaceDefinition> userSpaces;
    private AKernelSpaceExtensionDefinition<?> serviceSpace;

    public KernelConfigurationDefinition getConfiguration() {
        return configuration;
    }

    public Set<UUID> getInfoTypeIDs() {
        return this.infoTypeIDs;
    }

    public ThreadLocal<UserSpaceDefinition> getUserSpaces() {
        return this.userSpaces;
    }

    public AKernelSpaceExtensionDefinition<?> getServiceSpace() {
        return this.serviceSpace;
    }

    public void setServiceSpace(AKernelSpaceExtensionDefinition<?> serviceSpace) {
        this.serviceSpace = serviceSpace;
    }
}
