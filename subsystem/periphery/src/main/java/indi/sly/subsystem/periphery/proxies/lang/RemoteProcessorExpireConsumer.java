package indi.sly.subsystem.periphery.proxies.lang;

import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.system.common.lang.Consumer2;

@FunctionalInterface
public interface RemoteProcessorExpireConsumer extends Consumer2<RemoteDefinition, Long> {
}
