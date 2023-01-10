package indi.sly.system.boot;

import indi.sly.system.kernel.core.FactoryManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SpringConfiguration.class})
public class SystemBoot extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.sources(SystemBoot.class);
        return builder;
    }

    public static void main(String[] args) {
        SpringApplication system = new SpringApplication(null, new Class<?>[]{SystemBoot.class});
        ConfigurableApplicationContext applicationContext = system.run(args);

        try {
            applicationContext.getBean(FactoryManager.class);
        }catch (Exception e)
        {
            System.out.println("!!! e");
            System.out.println(e.getMessage());
        }

    }
}
