package indi.sly.system.services.center.lang;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.services.center.values.CenterDefinition;
import indi.sly.system.services.center.values.CenterStatusDefinition;

@FunctionalInterface
public interface StartFunction extends Consumer2<CenterDefinition, CenterStatusDefinition> {
}
