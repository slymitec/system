package indi.sly.system.services.nativeinterface.lang;

import indi.sly.system.common.lang.Function2;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceDefinition;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceStatusDefinition;

@FunctionalInterface
public interface CreateNativeInterfaceFunction extends Function2<NativeInterfaceDefinition, NativeInterfaceDefinition
        , NativeInterfaceStatusDefinition> {
}
