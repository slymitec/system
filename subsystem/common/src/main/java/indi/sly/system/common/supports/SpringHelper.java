package indi.sly.system.common.supports;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.objenesis.SpringObjenesis;

@Named
@Singleton
public class SpringHelper implements ApplicationContextAware {
    static {
        SpringHelper.objenesis = new SpringObjenesis();
    }

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringHelper.applicationContext = applicationContext;
    }

    public static <T> T getInstance(Class<T> clazz) throws BeansException {
        return SpringHelper.applicationContext.getBean(clazz);
    }

    private static SpringObjenesis objenesis;

    public static <T> T createInstance(Class<T> clazz) {
        return SpringHelper.objenesis.newInstance(clazz);
    }
}
