package indi.sly.system.services.center.lang;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.services.center.prototypes.CenterContentObject;

@FunctionalInterface
public interface CenterInitializerRunMethodConsumer extends Consumer2<CenterRunConsumer, CenterContentObject> {
}
