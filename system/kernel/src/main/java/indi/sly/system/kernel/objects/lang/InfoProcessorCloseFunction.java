package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function3;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;
import indi.sly.system.kernel.objects.values.InfoEntity;

@FunctionalInterface
public interface InfoProcessorCloseFunction extends Function3<InfoEntity, InfoEntity, TypeObject, InfoCacheEntity> {
}
