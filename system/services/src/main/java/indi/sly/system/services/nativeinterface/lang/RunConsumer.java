package indi.sly.system.services.nativeinterface.lang;

import indi.sly.system.common.lang.Consumer5;
import indi.sly.system.services.nativeinterface.prototypes.NativeInterfaceContentObject;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceDefinition;
import indi.sly.system.services.nativeinterface.values.NativeInterfaceStatusDefinition;

@FunctionalInterface
public interface RunConsumer extends Consumer5<NativeInterfaceDefinition, NativeInterfaceStatusDefinition, String,
        RunSelfConsumer, NativeInterfaceContentObject> {
}
