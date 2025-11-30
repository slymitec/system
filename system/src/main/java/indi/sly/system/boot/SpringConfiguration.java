package indi.sly.system.boot;

import indi.sly.system.common.ABase;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.nio.charset.StandardCharsets;

@Configuration
@ComponentScan(basePackages = SpringConfiguration.BASE_PACKAGES)
@EnableAsync
@EnableAutoConfiguration()
@EnableJpaRepositories(basePackages = SpringConfiguration.BASE_PACKAGES)
@EntityScan(basePackages = SpringConfiguration.BASE_PACKAGES)
@ServletComponentScan(basePackages = SpringConfiguration.BASE_PACKAGES)
public class SpringConfiguration extends ABase implements WebMvcConfigurer {
    public static final String BASE_PACKAGES = "indi.sly.*";

    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder){
        builder.addCustomConverter(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
