package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function6;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;

import java.util.UUID;

@FunctionalInterface
public interface InfoProcessorCreateChildFunction extends Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject,
        InfoCacheEntity, UUID, IdentifierDefinition> {
}
