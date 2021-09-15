package indi.sly.system.boot;

import indi.sly.system.common.ABase;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@ComponentScan(basePackages = SpringConfiguration.BASEPACKAGES)
@EnableAsync
@EnableAutoConfiguration()
@EnableJpaRepositories(basePackages = SpringConfiguration.BASEPACKAGES)
@EntityScan(basePackages = SpringConfiguration.BASEPACKAGES)
@ServletComponentScan(basePackages = SpringConfiguration.BASEPACKAGES)
public class SpringConfiguration extends ABase implements WebMvcConfigurer {
    public static final String BASEPACKAGES = "indi.sly.*";

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
