package indi.sly.clisubsystem.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SpringConfiguration.class})
public class CLISubSystemBoot {
    public static void main(String[] args) {
        SpringApplication cliSubSystem = new SpringApplication(null, new Class<?>[]{CLISubSystemBoot.class});
        cliSubSystem.run(args);
    }
}
