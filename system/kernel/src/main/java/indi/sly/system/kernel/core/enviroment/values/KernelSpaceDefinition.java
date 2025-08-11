package indi.sly.system.kernel.core.enviroment.values;

import indi.sly.system.common.supports.ObjectUtil;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Named
@Singleton
public class KernelSpaceDefinition extends ASpaceDefinition<KernelSpaceDefinition> {
    public KernelSpaceDefinition() {
        this.configuration = new KernelConfigurationDefinition();

        this.infoTypeIDs = new ConcurrentSkipListSet<>();

        this.userSpace = new ThreadLocal<>();
    }

    private final KernelConfigurationDefinition configuration;
    private final Set<UUID> infoTypeIDs;
    private final ThreadLocal<UserSpaceDefinition> userSpace;
    private AKernelSpaceExtensionDefinition<?> serviceSpace;

    public KernelConfigurationDefinition getConfiguration() {
        return configuration;
    }

    public Set<UUID> getInfoTypeIDs() {
        return this.infoTypeIDs;
    }

    public UserSpaceDefinition getUserSpace() {
        return this.userSpace.get();
    }

    public void setUserSpace(UserSpaceDefinition userSpace) {
        if (ObjectUtil.isAnyNull(userSpace)) {
            this.userSpace.remove();
        } else {
            this.userSpace.set(userSpace);
        }
    }

    public AKernelSpaceExtensionDefinition<?> getServiceSpace() {
        return this.serviceSpace;
    }

    public void setServiceSpace(AKernelSpaceExtensionDefinition<?> serviceSpace) {
        this.serviceSpace = serviceSpace;
    }
}
