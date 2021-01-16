package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Consumer5;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;

@FunctionalInterface
public interface RenameChildConsumer extends Consumer5<InfoEntity, TypeObject, InfoStatusDefinition,
        IdentificationDefinition, IdentificationDefinition> {
}
