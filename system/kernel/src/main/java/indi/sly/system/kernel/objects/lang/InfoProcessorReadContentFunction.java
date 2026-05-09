package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function4;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;

@FunctionalInterface
public interface InfoProcessorReadContentFunction extends Function4<byte[], byte[], InfoEntity, TypeObject, InfoCacheEntity> {
}
