package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function1;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;

@FunctionalInterface
public interface InfoProcessorParentFunction extends Function1<InfoObject, InfoStatusDefinition> {
}
