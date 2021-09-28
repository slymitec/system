package indi.sly.system.services.job.lang;

import indi.sly.system.common.lang.Function2;
import indi.sly.system.services.job.values.UserContextDefinition;
import indi.sly.system.services.job.values.UserContextRequestRawDefinition;

@FunctionalInterface
public interface UserContextProcessorCreateFunction extends Function2<UserContextDefinition, UserContextDefinition,
        UserContextRequestRawDefinition> {
}
