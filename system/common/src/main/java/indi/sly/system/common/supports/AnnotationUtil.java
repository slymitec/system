package indi.sly.system.common.supports;

import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public class AnnotationUtil {
    public static <A extends Annotation> @Nullable A getAnnotationFormOrNull(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return AnnotationUtils.getAnnotation(annotatedElement, annotationType);
    }

    public static <A extends Annotation> @Nullable A getAnnotationFormOrNull(Class<?> method, Class<A> annotationType) {
        return AnnotationUtils.getAnnotation(method, annotationType);
    }

    public static <A extends Annotation> @Nullable A getAnnotationFormOrNull(Method method, Class<A> annotationType) {
        return AnnotationUtils.getAnnotation(method, annotationType);
    }

    public static <A extends Annotation> @Nullable A getAnnotationFormThisAndSuperOrNull(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return AnnotationUtils.findAnnotation(annotatedElement, annotationType);
    }

    public static <A extends Annotation> @Nullable A getAnnotationFormThisAndSuperOrNull(Class<?> method, Class<A> annotationType) {
        return AnnotationUtils.findAnnotation(method, annotationType);
    }

    public static <A extends Annotation> @Nullable A getAnnotationFormThisAndSuperOrNull(Method method, Class<A> annotationType) {
        return AnnotationUtils.findAnnotation(method, annotationType);
    }
}
