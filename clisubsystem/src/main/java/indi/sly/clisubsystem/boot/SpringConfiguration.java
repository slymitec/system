package indi.sly.clisubsystem.boot;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@ComponentScan(basePackages = SpringConfiguration.BASE_PACKAGES)
@EnableAsync
@EnableAutoConfiguration()
public class SpringConfiguration {
    public static final String BASE_PACKAGES = "indi.sly.*";
}
