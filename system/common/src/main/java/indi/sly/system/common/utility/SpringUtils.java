package indi.sly.system.common.utility;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.objenesis.SpringObjenesis;

import javax.inject.Named;

@Named
public class SpringUtils implements ApplicationContextAware {
    static {
        SpringUtils.objenesis = new SpringObjenesis();
    }

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;

    }

    public static <T> T getInstance(Class<T> clazz) throws BeansException {
        return SpringUtils.applicationContext.getBean(clazz);
    }

    private static SpringObjenesis objenesis;

    public static <T> T createInstance(Class<T> clazz) {
        return SpringUtils.objenesis.newInstance(clazz);
    }
}
