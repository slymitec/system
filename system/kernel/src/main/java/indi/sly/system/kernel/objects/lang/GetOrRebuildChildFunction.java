package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function6;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;

import java.util.UUID;

@FunctionalInterface
public interface GetOrRebuildChildFunction extends Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject,
        InfoStatusDefinition, IdentificationDefinition, InfoStatusOpenDefinition> {
}
