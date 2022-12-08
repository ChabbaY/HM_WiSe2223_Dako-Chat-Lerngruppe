package edu.hm.dako.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * Main class of the API
 *
 * @author Linus Englert
 */
@SpringBootApplication
@Configuration
//@EnableJpaRepositories
public class APIStarter {
    public static void main(String[] args) {
        SpringApplication.run(APIStarter.class, args);
    }
}