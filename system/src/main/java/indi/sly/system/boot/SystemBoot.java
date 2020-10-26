package indi.sly.system.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SpringConfiguration.class})
public class SystemBoot {
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SystemBoot.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SystemBoot.class, args);
    }
}
