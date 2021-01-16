package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function4;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.DumpDefinition;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;

@FunctionalInterface
public interface DumpFunction extends Function4<DumpDefinition, DumpDefinition, InfoEntity, TypeObject,
        InfoStatusDefinition> {
}
