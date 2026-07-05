package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Consumer5;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;

@FunctionalInterface
public interface InfoProcessorRenameChildConsumer extends Consumer5<InfoEntity, TypeObject, InfoCacheEntity,
        IdentifierRecord, IdentifierRecord> {
}
