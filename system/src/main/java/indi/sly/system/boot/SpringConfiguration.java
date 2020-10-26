package indi.sly.system.boot;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableAutoConfiguration(/*exclude = { DataSourceAutoConfiguration.class }*/)
@ComponentScan(basePackages = SpringConfiguration.BASEPACKAGES)
@EntityScan(basePackages = SpringConfiguration.BASEPACKAGES)
@EnableJpaRepositories(basePackages = SpringConfiguration.BASEPACKAGES)
@ServletComponentScan(basePackages = SpringConfiguration.BASEPACKAGES)
public class SpringConfiguration implements WebMvcConfigurer {
    public static final String BASEPACKAGES = "indi.sly.*";

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }
}
