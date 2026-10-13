package indi.sly.system.kernel.core.environment.containers;

import indi.sly.system.common.supports.ObjectUtil;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Named
@Singleton
public class KernelSpace extends ASystemSpace {
    public KernelSpace() {
        this.configuration = new KernelConfiguration();
        this.infoTypeIds = new ConcurrentSkipListSet<>();
        this.userSpace = new ThreadLocal<>();
    }

    private final KernelConfiguration configuration;
    private final Set<UUID> infoTypeIds;
    private final ThreadLocal<@Nullable UserSpace> userSpace;
    @Nullable
    private AKernelExtensionSpace serviceSpace;

    public KernelConfiguration getConfiguration() {
        return configuration;
    }

    public Set<UUID> getInfoTypeIds() {
        return this.infoTypeIds;
    }

    public @Nullable UserSpace getUserSpace() {
        return this.userSpace.get();
    }

    public void setUserSpace(@Nullable UserSpace userSpace) {
        if (ObjectUtil.isAnyNull(userSpace)) {
            this.userSpace.remove();
        } else {
            this.userSpace.set(userSpace);
        }
    }

    public @Nullable AKernelExtensionSpace getServiceSpace() {
        return this.serviceSpace;
    }

    public void setServiceSpace(@Nullable AKernelExtensionSpace serviceSpace) {
        this.serviceSpace = serviceSpace;
    }
}
