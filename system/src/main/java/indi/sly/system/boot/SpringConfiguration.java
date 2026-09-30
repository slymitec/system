package indi.sly.system.boot;

import indi.sly.system.common.containers.AConfiguration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Configuration
@ComponentScan(basePackages = SpringConfiguration.BASE_PACKAGES)
@EnableAsync
@EnableAutoConfiguration()
@EnableJpaRepositories(basePackages = SpringConfiguration.BASE_PACKAGES)
@EntityScan(basePackages = SpringConfiguration.BASE_PACKAGES)
@ServletComponentScan(basePackages = SpringConfiguration.BASE_PACKAGES)
public class SpringConfiguration extends AConfiguration implements WebMvcConfigurer {
    public static final String BASE_PACKAGES = "indi.sly";

    @Bean
    public JsonMapperBuilderCustomizer longToStringCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule("LongToStringModule");
            module.addSerializer(Long.class, ToStringSerializer.instance);
            module.addSerializer(Long.TYPE, ToStringSerializer.instance);
            builder.addModule(module);
        };
    }
}