package indi.sly.clisubsystem.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SpringConfiguration.class})
public class SubSystemBoot {
    public static void main(String[] args) {
        SpringApplication cliSubSystem = new SpringApplication(null, new Class<?>[]{SubSystemBoot.class});
        cliSubSystem.run(args);
    }
}
