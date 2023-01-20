package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function5;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;

@FunctionalInterface
public interface InfoProcessorGetOrRebuildChildFunction extends Function5<InfoEntity, InfoEntity, InfoEntity,
        TypeObject, InfoStatusDefinition, IdentificationDefinition> {
}
