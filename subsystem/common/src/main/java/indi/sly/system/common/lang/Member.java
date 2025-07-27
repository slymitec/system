package indi.sly.system.common.lang;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Member {
    boolean inDeepCopy() default true;

    boolean inEqualsAndHashCode() default true;

    boolean inSerialize() default true;
}
