package indi.sly.subsystem.periphery.proxies.instances.security;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public interface IAccountAuthorizationProxy extends IProxy {
    @Nullable Map<Long, Long> getDate();

    boolean isLegal();
}
