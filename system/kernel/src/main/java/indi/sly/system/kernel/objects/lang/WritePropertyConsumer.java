package indi.sly.system.kernel.objects.lang;

import indi.sly.system.common.lang.Consumer4;
import indi.sly.system.common.lang.Consumer5;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoStatusDefinition;

import java.util.Map;

@FunctionalInterface
public interface WritePropertyConsumer extends Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, Map<String,
        String>> {
}
