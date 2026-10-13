package indi.sly.system.common.supports;

import indi.sly.system.common.ABase;
import indi.sly.system.common.lang.ConditionParametersException;
import org.apache.commons.lang3.ClassUtils;

public abstract class ClassUtil {
    public static String getName(Class<?> clazz) {
        return ClassUtils.getName(clazz);
    }

    public static String getSimpleName(Class<?> clazz) {
        return ClassUtils.getSimpleName(clazz);
    }

    public static String getAbbreviatedName(Class<? extends ABase> clazz, int lengthHint) {
        return ClassUtils.getAbbreviatedName(clazz, lengthHint);
    }

    public static boolean isThisOrSuperContain(Class<?> child, Class<?> parent) {
        if (ObjectUtil.isAnyNull(child, parent)) {
            throw new ConditionParametersException();
        }

        return parent.isAssignableFrom(child);
    }
}
