package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Consumer3;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;

@FunctionalInterface
public interface InfoProcessorExecuteContentConsumer extends Consumer3<InfoEntity, TypeObject, InfoCacheEntity> {
}
