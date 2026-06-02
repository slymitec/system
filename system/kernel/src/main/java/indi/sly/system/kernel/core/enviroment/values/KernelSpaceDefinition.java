package indi.sly.system.kernel.core.enviroment.values;

import indi.sly.system.common.supports.ObjectUtil;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Named
@Singleton
public class KernelSpaceDefinition extends ASpaceDefinition {
    public KernelSpaceDefinition() {
        this.configuration = new KernelConfigurationDefinition();
        this.infoTypeIds = new ConcurrentSkipListSet<>();
        this.userSpace = new ThreadLocal<>();
    }

    private final KernelConfigurationDefinition configuration;
    private final Set<UUID> infoTypeIds;
    private final ThreadLocal<UserSpaceDefinition> userSpace;
    private AKernelSpaceExtensionDefinition<?> serviceSpace;
    private long systemTimeOffset;

    public KernelConfigurationDefinition getConfiguration() {
        return configuration;
    }

    public Set<UUID> getInfoTypeIds() {
        return this.infoTypeIds;
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

    public long getSystemTimeOffset() {
        return systemTimeOffset;
    }

    public void setSystemTimeOffset(long systemTimeOffset) {
        this.systemTimeOffset = systemTimeOffset;
    }
}
