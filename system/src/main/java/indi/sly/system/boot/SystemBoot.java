package indi.sly.system.boot;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SpringConfiguration.class})
public class SystemBoot extends SpringBootServletInitializer {
    @NullMarked
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.sources(SystemBoot.class);
        return builder;
    }

    static void main(String[] args) {
        SpringApplication system = new SpringApplication(null, new Class<?>[]{SystemBoot.class});
        system.run(args);
    }
}
