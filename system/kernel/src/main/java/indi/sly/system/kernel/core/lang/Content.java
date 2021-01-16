package indi.sly.system.kernel.core.lang;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.lang.annotation.*;

@Inherited
@Named
@Retention(RetentionPolicy.RUNTIME)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Target({ElementType.TYPE})
public @interface Content {
}
