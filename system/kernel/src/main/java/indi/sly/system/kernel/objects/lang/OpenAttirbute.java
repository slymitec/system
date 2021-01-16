package indi.sly.system.kernel.objects.lang;

import indi.sly.system.kernel.objects.values.InfoStatusOpenAttributeType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface OpenAttirbute {
    long value() default InfoStatusOpenAttributeType.OPEN_EXCLUSIVE;
}
