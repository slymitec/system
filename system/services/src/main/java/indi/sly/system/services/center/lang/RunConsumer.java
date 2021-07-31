package indi.sly.system.services.center.lang;

import indi.sly.system.common.lang.Consumer5;
import indi.sly.system.services.center.prototypes.CenterContentObject;
import indi.sly.system.services.center.values.CenterDefinition;
import indi.sly.system.services.center.values.CenterStatusDefinition;

@FunctionalInterface
public interface RunConsumer extends Consumer5<CenterDefinition, CenterStatusDefinition, String,
        RunSelfConsumer, CenterContentObject> {
}
