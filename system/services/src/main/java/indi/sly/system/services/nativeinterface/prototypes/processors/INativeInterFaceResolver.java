package indi.sly.system.services.nativeinterface.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.IOrderlyResolver;
import indi.sly.system.services.nativeinterface.prototypes.wrappers.NativeInterfaceProcessorMediator;

public interface INativeInterFaceResolver extends IOrderlyResolver {
    void resolve(NativeInterfaceProcessorMediator processorMediator);
}
