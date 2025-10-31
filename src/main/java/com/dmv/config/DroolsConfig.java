package com.dmv.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

@Configuration
public class DroolsConfig {

    private static final Logger logger = LoggerFactory.getLogger(DroolsConfig.class);

    @Bean
    public KieServices kieServices() {
        return KieServices.Factory.get();
    }

    @Bean
    public KieContainer kieContainer(KieServices kieServices) throws IOException {
        logger.info("Initializing Drools KieContainer...");
        
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        
        // Load all .drl files from classpath:rules/
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:rules/*.drl");
        
        logger.info("Found {} rule files to load", resources.length);
        
        for (Resource resource : resources) {
            logger.info("Loading rule file: {}", resource.getFilename());
            kieFileSystem.write(ResourceFactory.newClassPathResource("rules/" + resource.getFilename()));
        }
        
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        
        if (kieBuilder.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            logger.error("Rule compilation errors: {}", kieBuilder.getResults().getMessages());
            throw new RuntimeException("Rule compilation failed: " + kieBuilder.getResults().getMessages());
        }
        
        KieModule kieModule = kieBuilder.getKieModule();
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        
        logger.info("Drools KieContainer initialized successfully");
        return kieContainer;
    }

    @Bean
    public KieSession kieSession(KieContainer kieContainer) {
        logger.info("Creating default KieSession...");
        KieSession kieSession = kieContainer.newKieSession();
        logger.info("Default KieSession created successfully");
        return kieSession;
    }
}