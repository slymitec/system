package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function1;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;

import java.util.UUID;

@FunctionalInterface
public interface InfoProcessorSelfFunction extends Function1<InfoEntity, InfoCacheEntity> {
}
