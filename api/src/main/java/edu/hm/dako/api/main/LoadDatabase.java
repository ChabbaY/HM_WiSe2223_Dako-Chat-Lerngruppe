package edu.hm.dako.api.main;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * initialization of data base values
 *
 * @author Linus Englert
 */
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {"edu.hm.dako.api.*"})
public class LoadDatabase {

}