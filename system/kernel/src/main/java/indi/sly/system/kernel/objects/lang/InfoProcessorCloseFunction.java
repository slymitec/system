package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function3;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;

@FunctionalInterface
public interface InfoProcessorCloseFunction extends Function3<InfoEntity, InfoEntity, TypeObject, InfoStatusDefinition> {
}
