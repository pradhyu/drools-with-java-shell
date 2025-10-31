package com.dmv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class DmvRulesEngineApplication {

    public static void main(String[] args) {
        // Check if --jshell flag is provided
        boolean jshellMode = false;
        for (String arg : args) {
            if ("--jshell".equals(arg)) {
                jshellMode = true;
                break;
            }
        }

        if (jshellMode) {
            System.out.println("Starting DMV Rules Engine with JShell integration...");
            // Start Spring Boot application which will trigger JShell command line runner
            SpringApplication.run(DmvRulesEngineApplication.class, args);
        } else {
            System.out.println("Starting DMV Rules Engine web application...");
            SpringApplication.run(DmvRulesEngineApplication.class, args);
        }
    }
}