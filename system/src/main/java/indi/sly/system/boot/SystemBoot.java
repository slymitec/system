package indi.sly.system.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SpringConfiguration.class})
public class SystemBoot extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication system = new SpringApplication(null, new Class<?>[]{SystemBoot.class});
        system.run(args);
    }
}
