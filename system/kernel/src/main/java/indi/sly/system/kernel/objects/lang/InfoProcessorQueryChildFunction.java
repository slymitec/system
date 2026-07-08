package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function5;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.*;

import java.util.Set;

@FunctionalInterface
public interface InfoProcessorQueryChildFunction extends Function5<Set<InfoSummaryRecord>, Set<InfoSummaryRecord>,
        InfoEntity, TypeObject,
        InfoCacheEntity, InfoWildcardRecord> {
}
