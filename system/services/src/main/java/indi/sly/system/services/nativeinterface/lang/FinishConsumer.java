package indi.sly.system.services.nativeinterface.lang;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceDefinition;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceStatusDefinition;

@FunctionalInterface
public interface FinishConsumer extends Consumer2<NativeInterfaceDefinition, NativeInterfaceStatusDefinition> {
}
