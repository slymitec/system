package indi.sly.system.services.center.lang;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.services.center.prototypes.CenterContentObject;

@FunctionalInterface
public interface InitializerConsumer extends Consumer2<RunSelfConsumer, CenterContentObject> {
}
