package indi.sly.subsystem.periphery.proxies.lang;

import indi.sly.subsystem.periphery.proxies.values.ProxyCacheEntity;
import indi.sly.system.common.lang.Function5;

@FunctionalInterface
public interface ProxyInvokeFunction extends Function5<Object, Object, ProxyCacheEntity, String, Class<?>, Object[]> {
}
