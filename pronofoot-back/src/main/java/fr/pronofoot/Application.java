package fr.pronofoot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"fr.pronofoot"})
public class Application {

    public static void main(String[] args) {
        SpringApplicationBuilder app = new SpringApplicationBuilder(Application.class);
        app.build().addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }
}