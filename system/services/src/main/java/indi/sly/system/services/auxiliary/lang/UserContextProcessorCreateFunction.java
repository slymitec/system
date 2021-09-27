package indi.sly.system.services.auxiliary.lang;

import indi.sly.system.common.lang.Function2;
import indi.sly.system.services.auxiliary.values.UserContextDefinition;
import indi.sly.system.services.auxiliary.values.UserContextRequestRawDefinition;

@FunctionalInterface
public interface UserContextProcessorCreateFunction extends Function2<UserContextDefinition, UserContextDefinition,
        UserContextRequestRawDefinition> {
}
