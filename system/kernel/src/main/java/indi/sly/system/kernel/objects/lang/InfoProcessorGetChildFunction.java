package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function5;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;

@FunctionalInterface
public interface InfoProcessorGetChildFunction extends Function5<InfoEntity, InfoEntity, InfoEntity,
        TypeObject, InfoCacheEntity, IdentifierDefinition> {
}
