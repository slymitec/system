package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Consumer4;
import indi.sly.system.common.lang.Function5;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;

import java.util.Set;
import java.util.function.Predicate;

@FunctionalInterface
public interface QueryChildFunction extends Function5<Set<InfoSummaryDefinition>, Set<InfoSummaryDefinition>,
        InfoEntity, TypeObject,
        InfoStatusDefinition, Predicate<InfoSummaryDefinition>> {
}
