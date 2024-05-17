package com.jvpars.codetip;

import com.jvpars.codetip.utils.DocumentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableScheduling
public class CodetipApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CodetipApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(DocumentService service) {

        return (args) -> {
// create folder here
            service.createPrimaryFolder();
        };
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CodetipApplication.class);
    }

}

