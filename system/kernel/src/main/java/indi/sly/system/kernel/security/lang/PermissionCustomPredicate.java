package indi.sly.system.kernel.security.lang;

import indi.sly.system.common.lang.Predicate2;
import indi.sly.system.kernel.security.values.AccessControlDefinition;

@FunctionalInterface
public interface PermissionCustomPredicate extends Predicate2<AccessControlDefinition, Long> {
}
