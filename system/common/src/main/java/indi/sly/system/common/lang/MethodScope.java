package indi.sly.system.common.lang;

import indi.sly.system.common.values.MethodScopeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodScope {
    long value() default MethodScopeType.WHATEVER;
}
