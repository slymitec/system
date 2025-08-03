package indi.sly.system.services.jobs.lang;

import indi.sly.system.common.lang.Function2;
import indi.sly.system.services.jobs.values.UserContextDefinition;
import indi.sly.system.services.jobs.values.UserContextRequestDefinition;

@FunctionalInterface
public interface UserContextProcessorCreateFunction extends Function2<UserContextDefinition, UserContextDefinition,
        UserContextRequestDefinition> {
}
