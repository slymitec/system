package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function3;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;
import indi.sly.system.kernel.security.values.SecurityDescriptorCacheEntity;

@FunctionalInterface
public interface InfoProcessorSecurityDescriptorFunction extends Function3<SecurityDescriptorCacheEntity, InfoEntity, TypeObject,
        InfoCacheEntity> {
}
