package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Function3;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;

@FunctionalInterface
public interface SecurityDescriptorFunction extends Function3<SecurityDescriptorObject, InfoEntity, TypeObject,
        InfoStatusDefinition> {
}
