package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function2;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;

import java.util.UUID;

@FunctionalInterface
public interface InfoProcessorSelfFunction extends Function2<InfoEntity, UUID, InfoStatusDefinition> {
}
